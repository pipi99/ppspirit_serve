package pp.spirit.security.springsecurity.handler;

import com.alibaba.fastjson.JSON;
import org.springframework.security.authentication.*;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import pp.spirit.base.base.ResultBody;
import pp.spirit.base.base.ResultCodeMessage;
import pp.spirit.base.properties.PPProperties;
import pp.spirit.base.utils.ContextUtils;
import pp.spirit.security.pojo.User;
import pp.spirit.security.service.UserService;
import pp.spirit.security.springsecurity.exception.CaptchaCheckException;
import pp.spirit.security.springsecurity.utils.SecurityAuthenticationCacheUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * 登录失败处理
 */
public class LoginFailureHandler implements AuthenticationFailureHandler {
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException e) throws IOException, ServletException {
        response.setContentType("application/json;charset=utf-8");
        PrintWriter out = response.getWriter();

        String username = request.getParameter("username");
        //如果不是锁定的异常，则纪录失败次数
        String result = "";
        if (!(e instanceof LockedException)){
            //纪录重试失败次数
            int retryTimes = SecurityAuthenticationCacheUtil.putRetryTimes(username);
            int appRetryTimes = ContextUtils.getBean(PPProperties.class).getUserLoginFailRetryTimes();
            //超过最大重试次数，锁定账号
            if(retryTimes>=appRetryTimes){
                User user = ContextUtils.getBean(UserService.class).findByUserName(username);
                if(user!=null){
                    user.setLocked(1);
                    ContextUtils.getBean(UserService.class).doLock(user);
                }
            }
        }
        if (e instanceof LockedException){
            int lockMinutes = ContextUtils.getBean(PPProperties.class).getUserLoginFailLockedMinutes();

            result = "账户已锁定，登录失败,锁定"+lockMinutes+"分钟";
        }else if(e instanceof CaptchaCheckException){
            result = e.getMessage();
        }else if(e instanceof BadCredentialsException){
            result = "用户名密码输入出错，登录失败";
        }else if (e instanceof DisabledException){
            result = "账户被禁用，登录失败";
        }else if (e instanceof CredentialsExpiredException) {
            result =  "密码过期，登录失败!";
        } else if (e instanceof InternalAuthenticationServiceException){
            result =  "登录失败!"+e.getMessage();
        }else {
            result =  "登录失败!";
        }

        out.write(JSON.toJSONString(ResultBody.result (ResultCodeMessage.UNAUTHORIZED,result)));
        out.flush();
        out.close();
    }
}
