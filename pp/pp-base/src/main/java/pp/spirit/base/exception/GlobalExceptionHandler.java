package pp.spirit.base.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import pp.spirit.base.base.ResultBody;
import pp.spirit.base.base.ResultCodeMessage;
import pp.spirit.base.utils.ContextUtils;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ValidationException;

/**
 * @author LiV
 * @Title:
 * @Package com.liv.exception
 * @Description: 全局异常处理, hold 父类即可拦截全部子类
 * @date 2020.3.26  10:54
 * @email 453826286@qq.com
 */

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    /**
     * 处理Model数据校验的异常
     * @param e
     * @return
     */
    @ExceptionHandler(ValidationException.class)
    public ResultBody ValidateException(ValidationException e){
        log.error("参数校验异常！原因是:"+e.getMessage(),e);
        ResultBody rb = ResultBody.result(ResultCodeMessage.VALIDATE_FAIL,e.getMessage());
        return rb;
    }

    /**
     * 处理其他异常
     * @return
     */
    @ExceptionHandler(Exception.class)
    public ResultBody globalException(Exception ex) {
        if ("org.apache.catalina.connector.ClientAbortException".equals(ex.getClass().getName())) {
            log.error("发生clientAbortException");
            return null;
        }
        log.error("发生异常！原因是:",ex);
        return  ResultBody.result(getStatus().value(),"异常",ex.getMessage());
    }

    private HttpStatus getStatus() {
        HttpServletRequest request = ContextUtils.getRequest();
        Integer statusCode = (Integer) request.getAttribute("javax.servlet.error.status_code");
        if (statusCode == null) {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }
        return HttpStatus.valueOf(statusCode);
    }
}
