package pp.spirit.base.utils;

/**
 * @author LiV
 * @Title:
 * @Package com.liv.api.base.utils
 * @Description: 函数回调
 * @date 2021.2.17  21:02
 * @email 453826286@qq.com
 */
public interface CallBack {
    /**
     * 回调执行方法
     */
    void executor();

    /**
     * 本回调任务名称
     * @return /
     */
    default String getCallBackName() {
        return Thread.currentThread().getId() + ":" + this.getClass().getName();
    }
}
