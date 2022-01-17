package pp.spirit.cache.util;


import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @author LiV
 * @Title:
 * @Package com.liv.api.base.utils
 * @Description: 简单缓存通用工具
 * @date 2020.5.29  15:02
 * @email 453826286@qq.com
 */
@Component
public class CacheUtil  implements ApplicationContextAware {
    public static RedisUtils redisUtils;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        CacheUtil.redisUtils = applicationContext.getBean("redisUtils", RedisUtils.class);
    }


    public static void set(String key,Object value,long expireSeconds){
        redisUtils.set( key, value,expireSeconds);
    }

    public static void set(String key,Object value){
        //7天
        redisUtils.set( key, value, 604800*60);
    }

    public static Object get(String key){
        return redisUtils.get( key);
    }

    public static void del(String key){
        redisUtils.del( key);
    }
    public static boolean del(final Collection<String> keys){
           return redisUtils.del( keys);
    }
    public static void del(String... key) {
        redisUtils.del( key);
    }

    public static void clear() {
        redisUtils.clear();
    }

    public static boolean expire(final String key, final long timeout, final TimeUnit unit) {
        return redisUtils.expire(key,timeout,unit);
    }
    public static boolean expire(final String key, final long timeout) {
        return redisUtils.expire(key,timeout);
    }
    public static long getExpire(String key){
        return redisUtils.getExpire(key);
    }

    public static int size() {
        return redisUtils.size();
    }

    public static Set<String> keys(String prefix) {
        return redisUtils.keys(prefix);
    }

    public static void deleteCachesByPrefix(String prefix){
        redisUtils.del(redisUtils.keys(prefix));
    }
}
