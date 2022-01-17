package pp.spirit.security.springsecurity.utils;

import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import pp.spirit.base.utils.ContextUtils;
import pp.spirit.security.pojo.*;
import pp.spirit.security.service.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
* @Description: 获取当前用户相关信息的工具类
* @author: liv
* @date  2021.12.17 15:40
*/
public class SecurityUtil {

    /**
    * @Description:  创建menu的权限标识
    * @author: liv
    * @date  2022.1.9 11:28
    */
    public static String createMenuPermission(Menu menu){
        //在这里指定菜单的规则，根据菜单属性去生成
        String  permission = "PERMISSION_";
        permission+= menu.getMenuId()!=null?menu.getMenuId():"";
        permission+= StringUtils.isNotEmpty(menu.getPath())?"_"+menu.getPath().replaceAll("/","_").toUpperCase():"";
        return permission;
    }

    /**
     * @Description: 获取当前用户的权限标识
     * @author: liv
     * @date  2022.1.14 10:16
     */
    public static List<Permission> getUserPermissions(String username){
        List<Permission> permissions =  getUserPermissionsFromCache(username);
        if(permissions!=null){
            return permissions;
        }
        return setUserPermissionsToCache( ContextUtils.getBean(UserService.class).findByUserName(username));
    }

    /**
    * @Description: 获取当前用户的权限标识
    * @author: liv
    * @date  2022.1.14 10:16
    */
    public static List<Permission> getCurrentUserPermissions(){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        List<Permission> permissions =  getUserPermissionsFromCache(username);
        if(permissions!=null){
            return permissions;
        }
        return setUserPermissionsToCache(getCurrentUser());
    }
    /**
     * @description 获取当前登录用户信息
     * @param
     * @return User
     * @author liv
     * @date   2021.12.17 15:33
     */
    public static User getCurrentUser(){
        return ContextUtils.getBean(UserService.class).findByUserName(SecurityContextHolder.getContext().getAuthentication().getName());
    }

    /**
    * @Description: 获取当前用户的菜单 
    * @author: liv
    * @date  2022.1.13 10:35
    */
    public static List<Menu> getCurrentUserMenus(){
        return getCurrentUserPermissions()
                .stream()
                .filter((permission -> permission.getMenu()!=null))
                .map((permission)->permission.getMenu())
                .collect(Collectors.toList());
    }

    /**
    * @Description: 从缓存获取权限 
    * @author: liv
    * @date  2022.1.14 10:47
    */
    private static List<Permission> getUserPermissionsFromCache(String username){
        List<Permission> permissions = SecurityAuthenticationCacheUtil.getUserPermissions(username);
        return permissions;
    }

    /**
    * @Description: 设置权限到缓存
    * @author: liv
    * @date  2022.1.14 10:47
    */
    private static List<Permission> setUserPermissionsToCache(User sysUser){
        List<Permission> permissions = new ArrayList<>();
        // 用户
        permissions.addAll(sysUser.getPermissions());
        // 用户组
        permissions.addAll(sysUser.getGroups().stream().map(Group::getPermissions).flatMap(List::stream).collect(Collectors.toList()));
        // 用户角色
        permissions.addAll(sysUser.getRoles().stream().map(Role::getPermissions).flatMap(List::stream).collect(Collectors.toList()));
        // 用户组角色
        permissions.addAll(sysUser.getGroups().stream().map(Group::getRoles).flatMap(List::stream).map(Role::getPermissions).flatMap(List::stream).collect(Collectors.toList()));
        //缓存起来
        return SecurityAuthenticationCacheUtil.putUserPermissions(sysUser.getUserName(),permissions);
    }
}
