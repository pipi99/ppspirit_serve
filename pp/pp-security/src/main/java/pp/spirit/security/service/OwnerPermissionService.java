package pp.spirit.security.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pp.spirit.base.base.BaseService;
import pp.spirit.base.utils.Collection2TreeUtils;
import pp.spirit.base.utils.ContextUtils;
import pp.spirit.security.dao.jpa.OwnerPermissionRepository;
import pp.spirit.security.dao.mapper.OwnerPermissionMapper;
import pp.spirit.security.pojo.*;
import pp.spirit.security.springsecurity.utils.SecurityAuthenticationCacheUtil;
import pp.spirit.security.springsecurity.utils.SecurityConst;

import javax.transaction.Transactional;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class OwnerPermissionService extends BaseService<Long, OwnerPermission, OwnerPermissionMapper, OwnerPermissionRepository> {

    @Autowired
    private MenuService menuService;

    @Autowired
    private PermissionService permissionService;

    @Transactional
    public boolean savePermission(List<OwnerPermission> ownerPermissionList){
        //删除再插入
        if(ownerPermissionList!=null&&ownerPermissionList.size()>0){
            //清除缓存
            this.removeAndClearCache(ownerPermissionList.get(0).getOwnerId());
            this.saveBatch(ownerPermissionList);
        }
        return true;
    }

    /**
    * @Description: 赋权的时候，清除相关用户的缓存
    * @author: liv
    * @param ownerId 用户、角色、用户组
    * @date  2022.1.15 09:13
    */
    public void removeAndClearCache(Long ownerId){
        //清除某些用户的缓存
        Optional<Group> groupOptional;
        Optional<Role> roleOptional;
        User user ;

        //是否用户权限
        if((user = ContextUtils.getBean(UserService.class).getById(ownerId))!=null){
            SecurityAuthenticationCacheUtil.deleteUserPermissions(user.getUserName());
        //是否组权限
        }else if((groupOptional = ContextUtils.getBean(GroupService.class).getRepository().findById(ownerId)).isPresent()){
            Group group = groupOptional.get();
            if(group.getUsers()!=null&&group.getUsers().size()>0){
                group.getUsers().stream().forEach(_user->SecurityAuthenticationCacheUtil.deleteUserPermissions(_user.getUserName()));
            }
        //是否角色权限
        }else if((roleOptional = ContextUtils.getBean(RoleService.class).getRepository().findById(ownerId)).isPresent()){
            Role role = roleOptional.get();
            if(role.getUsers()!=null&&role.getUsers().size()>0){
                role.getUsers().stream().forEach(_user->SecurityAuthenticationCacheUtil.deleteUserPermissions(_user.getUserName()));
            }
            //组角色
            if(role.getGroups()!=null&&role.getGroups().size()>0){
                role.getGroups().stream().map(_group -> _group.getUsers()).flatMap(Collection::stream).forEach(_user->SecurityAuthenticationCacheUtil.deleteUserPermissions(_user.getUserName()));
            }
        }

        OwnerPermissionQuery query = new OwnerPermissionQuery();
        query.setOwnerId(ownerId);
        this.remove(query.getQueryWrapper());
    }
    /**
    * @Description: 赋权时使用
    * @author: liv
    * @date  2022.1.13 10:47
    */
    public List<Permission> treeList() throws Exception {
        // 首先获取菜单
        MenuQuery menuQuery = new MenuQuery();
        menuQuery.setIsHidden(0);
        //分配需授权访问的菜单
        menuQuery.setAccessCtrl(2);
        List<Menu> menus = menuService.getRepository().findAll(menuQuery.getSpecification());
        menus = Collection2TreeUtils.getTree(menus,"menuId","parentId","children");

        //菜单转为权限
        List<Permission> permissions = mapMenuToPermission(menus);
        //获取其他权限
        QueryWrapper queryWrapper =  new QueryWrapper();
        queryWrapper.eq("RESOURCE_TYPE",SecurityConst.PERMISSION_TYPE_CUSTOMIZE);
        List<Permission> otherPermissions = permissionService.list(queryWrapper);
        Permission other = new Permission();
        other.setPermissionId(-1L);
        other.setRemark("其他权限");
        other.setChildren(otherPermissions);

        //合并
        permissions.add(other);
        return permissions;
    }

    private List<Permission>  mapMenuToPermission(List<Menu> menus){
        List<Permission> permissions = menus.stream().map((menu)->{
            Permission permission = menu.getPermission().get(0);
            if(menu.getChildren()!=null&&menu.getChildren().size()>0){
                permission.setChildren(mapMenuToPermission(menu.getChildren()));
            }
            return permission;
        }).collect(Collectors.toList());
        return permissions;
    }
}
