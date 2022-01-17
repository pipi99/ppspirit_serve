package pp.spirit.base.base;

import io.swagger.annotations.ApiModel;
import lombok.Data;

/**
 * @author LiV
 * @Title:
 * @Package com.liv.base
 * @Description: 响应实体
 * @date 2020.3.26  11:31
 * @email 453826286@qq.com
 */
@Data
@ApiModel(value = "ResultBody",description = "统一返回结果实体")
public class ResultBody {
    /**
     * 响应代码
     */
    private int code;

    /**
     * 响应消息
     */
    private String message;

    /**
     * 响应结果
     */
    private Object data;

    /**
     * 成功
     *
     * @return
     */
    public static ResultBody success() {
        return success("操作成功");
    }

    /**
     * 成功
     * @param data
     * @return
     */
    public static ResultBody success(Object data) {
        ResultBody rb = new ResultBody();
        rb.setCode(ResultCodeMessage.SUCCESS.getCode());
        rb.setMessage(ResultCodeMessage.SUCCESS.getMessage());
        rb.setData(data);
        return rb;
    }

    /**
     * 失败
     *
     * @return
     */
    public static ResultBody fail() {
        return fail("");
    }

    /**
     * 失败
     */
    public static ResultBody fail(Object data) {
        ResultBody rb = new ResultBody();
        rb.setCode(ResultCodeMessage.FAILED.getCode());
        rb.setMessage(ResultCodeMessage.FAILED.getMessage());
        rb.setData(data);
        return rb;
    }

    /**
     * 结果
     */
    public static ResultBody result(int code, String message) {
        return result( code,  message,null) ;
    }

    /**
     * 结果
     */
    public static ResultBody result(int code, String message, Object data) {
        ResultBody rb = new ResultBody();
        rb.setCode(code);
        rb.setMessage(message);
        rb.setData(data);
        return rb;
    }

    /**
     * 结果
     */
    public static ResultBody result(ResultCodeMessage codeMessage) {
        return result( codeMessage.getCode(),  codeMessage.getMessage(),null) ;
    }

    /**
     * 结果
     */
    public static ResultBody result(ResultCodeMessage codeMessage, Object data) {
        return result( codeMessage.getCode(),  codeMessage.getMessage(),data) ;
    }
}
