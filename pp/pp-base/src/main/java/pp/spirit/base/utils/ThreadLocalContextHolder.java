package pp.spirit.base.utils;

/**
 * @author LW
 * @title: ThreadLocalContextHolder
 * @projectName spirit
 * @description: TODO
 * @date 2021.11.2019:15
 */
public class ThreadLocalContextHolder {
    /**
     * 不同业务设置不同的业务场景，如：业务A设置值为1，业务B设置值为2...
     */
    private static ThreadLocal<Object> tl = new ThreadLocal<>();

    public static Object get() {
        return tl.get();
    }

    public static void set(Object scene) {
        tl.set(scene);
    }

    public static void remove() {
        tl.remove();
    }
}
