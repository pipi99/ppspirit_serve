package pp.spirit.base.utils;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * @author LiV
 * @Title:
 * @Package com.liv.web.utils
 * @Description: 工程上下文工具类
 * @date 2020.4.19  16:11
 * @email 453826286@qq.com
 */
@Component
public class ContextUtils implements ApplicationContextAware {
    private static ApplicationContext applicationContext;
    private static final List<CallBack> CALL_BACKS = new ArrayList<>();
    private static boolean addCallback = true;


    /**
     * 针对 某些初始化方法，在SpringContextHolder 未初始化时 提交回调方法。
     * 在SpringContextHolder 初始化后，进行回调使用
     *
     * @param callBack 回调函数
     */
    public synchronized static void addCallBacks(CallBack callBack) {
        if (addCallback) {
            CALL_BACKS.add(callBack);
        } else {
            callBack.executor();
        }
    }

    /**
     * 获取request对象
     */
    public static HttpServletRequest getRequest() {
        return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
    }

    /**
     * 获取Response对象
     */
    public static HttpServletResponse getResponse() {
        return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();
    }

    /**
     * 获取Session对象
     */
    public static HttpSession getSession() {
        return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest().getSession();
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        if(ContextUtils.applicationContext == null) {
            ContextUtils.applicationContext = applicationContext;
            if (addCallback) {
                for (CallBack callBack : CALL_BACKS) {
                    callBack.executor();
                }
                CALL_BACKS.clear();
            }
            addCallback = false;
        }
    }

    //获取applicationContext
    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    //通过name获取 Bean.
    public static Object getBean(String name){
        return getApplicationContext().getBean(name);
    }

    //通过class获取Bean.
    public static <T> T getBean(Class<T> clazz){
        return getApplicationContext().getBean(clazz);
    }

    //通过name,以及Clazz返回指定的Bean
    public static <T> T getBean(String name,Class<T> clazz){
        return getApplicationContext().getBean(name, clazz);
    }

    /**
     *
     * @param serviceName
     * @param methodName
     * @param params
     * @return
     * @throws Exception
     */
    public static Object springInvokeMethod(String serviceName, String methodName, Object... params) throws Exception {
        Object service = getBean(serviceName);
        Class<? extends Object>[] paramClass = null;
        if (params != null) {
            int paramsLength = params.length;
            paramClass = new Class[paramsLength];
            for (int i = 0; i < paramsLength; i++) {
                paramClass[i] = params[i].getClass();
            }
        }
        // 找到方法
        Method method = ReflectionUtils.findMethod(service.getClass(), methodName, paramClass);
        // 执行方法
        return ReflectionUtils.invokeMethod(method, service, params);
    }


}
