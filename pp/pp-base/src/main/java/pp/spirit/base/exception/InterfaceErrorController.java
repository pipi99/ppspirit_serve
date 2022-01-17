package pp.spirit.base.exception;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pp.spirit.base.base.ResultBody;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;

/**
 * @Title: InterfaceErrorController
 * @Description: 处理路径映射异常
 * @author LW
 * @date 2021.11.19 14:25
 */
@RestController("InterfaceErrorController_404")
@Api(value = "404异常统一处理",tags = {"404异常统一处理"})
@Slf4j
public class InterfaceErrorController implements ErrorController {

    // 路径与配置文件路径一致
    @ApiOperation(value = "统一路径异常处理", notes="统一路径异常处理")
    @RequestMapping(value = "/error")
    public ResultBody handleError(HttpServletRequest request) {
        Integer status = Integer.parseInt(request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE)+"");
        String message = request.getAttribute(RequestDispatcher.ERROR_MESSAGE)+"";
        log.error(status+" : "+message,InterfaceErrorController.class);
        return ResultBody.result(status,message,"访问异常");
    }
}
