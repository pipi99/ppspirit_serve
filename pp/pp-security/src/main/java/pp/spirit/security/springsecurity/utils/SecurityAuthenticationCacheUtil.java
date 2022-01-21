package pp.spirit.security.springsecurity.utils;


import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import pp.spirit.base.properties.PPProperties;
import pp.spirit.cache.util.CacheUtil;
import pp.spirit.security.pojo.Menu;
import pp.spirit.security.pojo.Permission;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author LiV
 * @Title:
 * @Package com.liv.spring-security.cache
 * @Description:
 * @date 2020.4.26  14:55
 * @email 453826286@qq.com
 */
@Component
public class SecurityAuthenticationCacheUtil implements ApplicationContextAware {

    private static JwtTokenUtil jwtTokenUtil;
    private static PPProperties ppProperties;

    //用户登录信息缓存
    public final static String SPRING_SECURITY_JWT_CACHE_NAME = "spring-security-cache:JwtCacheName:";
    public final static String SPRING_SECURITY_AUTHENTICATION_CACHE_NAME = "spring-security-cache:AuthenticationCacheName:";
    public final static String SPRING_SECURITY_AUTHORIZATION_CACHE_NAME = "spring-security-cache:AuthorizationCacheName:";

    //菜单缓存
    public final static String SPRING_SECURITY_MENU_CACHE_NAME = "spring-security-cache:MenuCacheName:";

    //密钥缓存
    public final static String SPRING_SECURITY_RSA_CACHE_NAME = "spring-security-cache:RsaCacheName:";

    //用户登录
    public final static String LOGIN_SUCCESS_SUBJECT_CACHE = "spring-security-cache:login-success-subject-cache:";

    //密码重试缓存
    public final static String PASSWORD_RETRY_CACHE = "spring-security-cache:password-retry-cache:";

    //角色权限缓存
    public final static String ROLE_PERMISSION_CACHE = "spring-security-cache:role-permission-cache:";

    //需授权菜单缓存
    public final static String MENU_NEED_PERMISSION_CACHE = "spring-security-cache:menu-need-permission-cache:";

    /**
    * @Description: 删除跟系统有关的缓存，系统重启的时候会执行
    * @author: liv
    * @date  2022.1.14 17:14
    */
    public static void deleteAllCache(){
        CacheUtil.deleteCachesByPrefix("spring-security-cache:");
    }

    /**
     * 缓存菜单
     **/
    public static List<Menu> putMenus(List<Menu> menus) {
        String key = SPRING_SECURITY_MENU_CACHE_NAME;
        CacheUtil.set(key,menus);
        return menus;
    }

    /**
     * 获取菜单
     **/
    public static List<Menu> getMenus()  {
        String key = SPRING_SECURITY_MENU_CACHE_NAME;
        Object o = CacheUtil.get(key);
        return o==null?null:(List<Menu>)o;
    }

    /**
     * 删除菜单
     **/
    public static void deleteMenus()  {
        String key = SPRING_SECURITY_MENU_CACHE_NAME;
        CacheUtil.del(key);
    }

    /**
     * 缓存用户权限
     **/
    public static List<Permission> putUserPermissions(String username, List<Permission> permissions) {
        String key = SPRING_SECURITY_AUTHORIZATION_CACHE_NAME+username;
        CacheUtil.set(key,permissions,ppProperties.getTokenRefreshTokenTimeoutMinutes()*60);
        return permissions;
    }

    /**
     * 获取用户权限
     **/
    public static List<Permission> getUserPermissions(String username)  {
        String key = SPRING_SECURITY_AUTHORIZATION_CACHE_NAME+username;
        Object o = CacheUtil.get(key);
        return o==null?null:(List<Permission>)o;
    }

    /**
     * 删除用户权限
     **/
    public static void deleteUserPermissions(String username)  {
        String key = SPRING_SECURITY_AUTHORIZATION_CACHE_NAME+username;
        CacheUtil.del(key);
    }

    /**
     * 缓存重试次数
     **/
    public static int putRetryTimes(String username) {
        String key = PASSWORD_RETRY_CACHE+username;
        AtomicInteger ai = getRetryTimes(username);
        if(ai == null){
            ai = new AtomicInteger(0);
        }
        int val = ai.addAndGet(1);
        CacheUtil.set(key,ai,ppProperties.getUserLoginFailLockedMinutes()*60);
        return val;
    }

    /**
     * 获取重试次数
     **/
    public static AtomicInteger getRetryTimes(String username)  {
        String key = PASSWORD_RETRY_CACHE+username;
        Object o = CacheUtil.get(key);
        return o==null?null:(AtomicInteger)o;
    }

    /**
     * 删除重试次数
     **/
    public static void deleteRetryTimes(String username)  {
        String key = PASSWORD_RETRY_CACHE+username;
        CacheUtil.del(key);
    }

    /**
     * 缓存密钥对
     **/
    public static void putKeyPair(Map<String,Object> rsaKeyPair) throws Exception {
        String key = SPRING_SECURITY_RSA_CACHE_NAME+RSAUtils.getPublicKey(rsaKeyPair);
        CacheUtil.set(key,rsaKeyPair,60);
    }

    /**
     * 获取密钥
     **/
    public static Map<String,Object> getKeyPair(String publicKey)  {
        String key = SPRING_SECURITY_RSA_CACHE_NAME+publicKey;
        Object o = CacheUtil.get(key);
        return o==null?null:(Map<String,Object>)o;
    }

    /**
     * 删除密钥
     **/
    public static void deleteKeyPair(String publicKey)  {
        String key = SPRING_SECURITY_RSA_CACHE_NAME+publicKey;
        CacheUtil.del(key);
    }

    /**
     * 缓存jwttoken
     **/
    public static void putToken(String jwtId,String token){
        String key = SPRING_SECURITY_JWT_CACHE_NAME+jwtId;
        CacheUtil.set(key,token,ppProperties.getTokenRefreshTokenTimeoutMinutes()*60);
    }

    /**
     * 获取jwttoken
     * 这里的key是  jwt_id  的值
     * @param jwtId 这里的key是  jwt_id  的值
     **/
    public static String getToken(String jwtId){
        String key = SPRING_SECURITY_JWT_CACHE_NAME+jwtId;
        Object o = CacheUtil.get(key);
        return o==null?null:o+"";
    }

    /**
     * 删除jwttoken
     **/
    public static void deleteToken(String token){
        String key = SPRING_SECURITY_JWT_CACHE_NAME+jwtTokenUtil.getJwtIdFromToken(token);
        CacheUtil.del(key);
    }

    /**
     * 缓存authentication
     **/
    public static void putAuthentication(String jwtId, Authentication authentication){
        String key = SPRING_SECURITY_AUTHENTICATION_CACHE_NAME+jwtId;
        CacheUtil.set(key,authentication,ppProperties.getTokenRefreshTokenTimeoutMinutes()*60);
    }

    /**
     * 获取authentication
     **/
    public static Authentication getAuthentication(String jwtId){
        String key = SPRING_SECURITY_AUTHENTICATION_CACHE_NAME+jwtId;
        Object o = CacheUtil.get(key);
        return o==null?null:(Authentication)o;
    }

    /**
     * 删除authentication
     **/
    public static void deleteAuthentication(String jwtId){
        String key = SPRING_SECURITY_AUTHENTICATION_CACHE_NAME+jwtId;
        CacheUtil.del(key);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        SecurityAuthenticationCacheUtil.jwtTokenUtil = applicationContext.getBean(JwtTokenUtil.class);
        SecurityAuthenticationCacheUtil.ppProperties = applicationContext.getBean(PPProperties.class);
    }
}
