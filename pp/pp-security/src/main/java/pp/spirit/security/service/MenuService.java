package pp.spirit.security.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.AntPathMatcher;
import pp.spirit.base.base.BaseService;
import pp.spirit.base.utils.Collection2TreeUtils;
import pp.spirit.security.dao.jpa.MenuRepository;
import pp.spirit.security.dao.mapper.MenuMapper;
import pp.spirit.security.pojo.Menu;
import pp.spirit.security.pojo.Permission;
import pp.spirit.security.pojo.User;
import pp.spirit.security.pojo.domainmodel.MenuComponent;
import pp.spirit.security.springsecurity.decision.ResourcesAcceptConstantsUtil;
import pp.spirit.security.springsecurity.decision.ResourcesAcceptTypes;
import pp.spirit.security.springsecurity.utils.SecurityAuthenticationCacheUtil;
import pp.spirit.security.springsecurity.utils.SecurityConst;
import pp.spirit.security.springsecurity.utils.SecurityUtil;

import javax.transaction.Transactional;
import java.io.Serializable;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MenuService extends BaseService<Long, Menu, MenuMapper, MenuRepository> {

    @Autowired
    PermissionService permissionService;

    @Override
    @Transactional
    public boolean save(Menu entity) {
        //删除缓存
        this.clearMenuCache();

        //执行保存
        int result = this.getMapper().insert(entity);

        //保存权限
        Permission permission = new Permission();
        permission.setResourceType(SecurityConst.PERMISSION_TYPE_MENU);
        permission.setPermission(SecurityUtil.createMenuPermission(entity));
        permission.setRemark(entity.getMenuName());
        permission.setResourceId(entity.getMenuId());
        permissionService.save(permission);
        return SqlHelper.retBool(result);
    }

    @Override
    @Transactional
    public boolean updateById(Menu entity) {
        //删除缓存
        this.clearMenuCache();

        Menu menu = this.getRepository().getById(entity.getMenuId());
        List<Permission> permissions = menu.getPermission();
        if(permissions!=null&&permissions.size()>0){
            permissionService.removeById(permissions.get(0).getPermissionId());
        }

        int result = this.getMapper().updateById(entity);
        //保存权限
        Permission permission = new Permission();
        permission.setResourceType(SecurityConst.PERMISSION_TYPE_MENU);
        permission.setPermission(SecurityUtil.createMenuPermission(entity));
        permission.setRemark(entity.getMenuName());
        permission.setResourceId(entity.getMenuId());

        permissionService.save(permission);

        return SqlHelper.retBool(result);
    }

    @Override
    @Transactional
    public boolean removeById(Serializable id) {
        //删除缓存
        this.clearMenuCache();

        Menu menu = this.getRepository().getById((Long)id);
        List<Permission> permissions = menu.getPermission();
        if(permissions!=null&&permissions.size()>0){
            permissionService.removeById(permissions.get(0).getPermissionId());
        }
        int result = this.getMapper().deleteById(id);
        return SqlHelper.retBool(result);
    }

    /**
     * @description  获取当前登录用户的菜单信息
     * @param
     * @return
     * @author liv
     * @date   2021.12.17 15:27
     */
    public List<Menu> getCurrentUserMenus() throws Exception {
        //开放链接和免授权链接
        QueryWrapper<Menu> qw = new QueryWrapper<>();
        qw.eq("ENABLED",1);

        User user = SecurityUtil.getCurrentUser();
        List<Menu> menus = null;

        //如果不是管理员或者超级管理员
        boolean isAdminOrSuerAdmin = user.getRoles()!=null&&user.getRoles().stream().anyMatch((role)->role.getRoleName().equalsIgnoreCase(SecurityConst.ROLE_ADMINISTRATOR)||role.getRoleName().equalsIgnoreCase(SecurityConst.ROLE_SUPER_ADMINISTRATOR));
        if(isAdminOrSuerAdmin){
            menus = this.list(qw);
        }else {
            qw.and((menuQueryWrapper -> {
                menuQueryWrapper.eq("ACCESS_CTRL", SecurityConst.MENU_ANONYMOUS).or().eq("ACCESS_CTRL", SecurityConst.MENU_LOGIN);
            }));
            menus = this.list(qw);
            //授权链接
            List<Menu> authMenus = SecurityUtil.getCurrentUserMenus();
            //去重
            menus.stream().filter((menu)->!authMenus.stream().anyMatch((menu1)->menu.getMenuId() == menu1.getMenuId()));
            //合并
            menus.addAll(authMenus);
        }

        //转成树结构
        return Collection2TreeUtils.getTree(menus,"menuId","parentId","children");
    }

    /**
    * @Description: 获取当前登录用户的菜单信息-配合 vben-router
    * @author: liv
    * @date  2022.1.14 15:08
    */
    public List<MenuComponent> getCurrentUserMenuComponents() throws Exception {
        return getCurrentUserMenus().stream().map(menu -> new MenuComponent().transform(menu)).collect(Collectors.toList());
    }

    /**
     * @Description: 增删改，清除缓存
     * @author: liv
     * @date  2022.1.15 09:28
     */
    private  void clearMenuCache() {
        //清除菜单缓存
        SecurityAuthenticationCacheUtil.deleteMenus();
    }

    /**
    * @Description: 权限的过滤器使用，采用缓存处理菜单列表
    * @author: liv
    * @date  2022.1.15 09:28
    */
    private  List<Menu> getMenusFromCache() {
        //获取所有菜单,优先缓存
        List<Menu> menus = SecurityAuthenticationCacheUtil.getMenus();
        if (menus == null) {
            menus = this.getRepository().findAll();
            SecurityAuthenticationCacheUtil.putMenus(menus);
        }
        return menus;
    }
    /**
     * 查询给定资源id的权限标识，如为空，则返回“禁止访问的权限标识” ,精确定位权限
     * @param menuId
     * @return
     */
    public Permission getPermissionById(Long menuId){
        //获取所有菜单,优先缓存
        List<Menu> menus = getMenusFromCache();

        Optional<Menu> optional =  menus.stream().filter(m -> m.getMenuId().equals(menuId)).findFirst();
        if(optional.isPresent()){
            Menu menu = optional.get();
            //检查访问控制方式，如果需要授权
            if(menu.getAccessCtrl().equals(ResourcesAcceptTypes.NEED_AUTHORIZE.getValue())){
                //返回当前所需的权限
                return  menu.getPermission().get(0);
                //如果不需要授权
            }else{
                return new Permission(ResourcesAcceptConstantsUtil.ResourcesAcceptConstants.PERMISSION_FOR_NEED_LOGIN.getValue());
            }
        }
        return new Permission(ResourcesAcceptConstantsUtil.ResourcesAcceptConstants.PERMISSION_FOR_NO_RESOURCE_ID.getValue());
    }

    /**
     * 查询给定资源id的权限标识，如为空，则返回“禁止访问的权限标识” ,精确定位权限
     * @param requestUrl
     * @return
     */
    public Permission getPermissionByRequestUrl(String requestUrl){
        AntPathMatcher antPathMatcher = new AntPathMatcher();
        //获取所有菜单,优先缓存
        //获取所有菜单,优先缓存
        List<Menu> menus = getMenusFromCache();

        return menus.stream()
            //1 是菜单，0 是目录
            .filter(menu -> {
//                Assert.isTrue((menu.getIsMenu()!=1||(menu.getIsMenu()==1&& StringUtils.isNotEmpty(menu.getPath()))),"【"+menu.getMenuName()+"]"+"菜单地址不能为空！");
                return  menu.getIsMenu()==1&& StringUtils.isNotEmpty(menu.getPath())&&antPathMatcher.match(menu.getPath(), requestUrl);
            })
            .map(menu->{
                //如果需要授权
                if(menu.getAccessCtrl().equals(ResourcesAcceptTypes.NEED_AUTHORIZE.getValue())){
                    //返回当前所需的权限
                    return menu.getPermission().get(0);
                    //如果不需要授权
                }else{
                    return new Permission(ResourcesAcceptConstantsUtil.ResourcesAcceptConstants.PERMISSION_FOR_NEED_LOGIN.getValue());
                }
                //如果没有 匹配到，则返回 需要登录的权限需求
            })
            .findFirst()
            .orElse( new Permission(ResourcesAcceptConstantsUtil.ResourcesAcceptConstants.PERMISSION_FOR_NEED_LOGIN.getValue()));
    }
}