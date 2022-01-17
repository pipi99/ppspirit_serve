package pp.spirit.base.base;

/**
 * @author LiV
 * @Title:
 * @Package com.liv.base
 * @Description: 返回结果枚举
 * @date 2020.3.26  11:34
 * @email 453826286@qq.com
 */
public enum ResultCodeMessage {
    VALIDATE_FAIL("参数校验异常", 9400),
    FAILED("操作失败", 9500),

    SUCCESS("操作成功", 200),

    INVALID ("参数错误", 400),
    UNAUTHORIZED("未经许可的", 401),
    FORBIDDEN("禁止访问的", 403),
    NOT_FOUND("无效路径",404),

    ERROR("服务器异常", 500),
    DATA_QUERY_ERROR ("数据获取失败", 501);

    private String message;
    private int code;

    private ResultCodeMessage(String message, int code) {
        this.message = message;
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public int getCode() {
        return code;
    }
}