package pp.spirit.security.springsecurity.handler;

import com.alibaba.fastjson.JSON;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import pp.spirit.base.base.ResultBody;
import pp.spirit.base.utils.ContextUtils;
import pp.spirit.security.springsecurity.utils.JwtTokenUtil;
import pp.spirit.security.springsecurity.utils.SecurityAuthenticationCacheUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

/**
 * 登录成功处理
 */
public class LoginSuccessHandler implements AuthenticationSuccessHandler {
    @Override
    public void onAuthenticationSuccess(HttpServletRequest req, HttpServletResponse resp, Authentication authentication) throws IOException, ServletException {

        //清除用户相关缓存
        clearCache(authentication);
        
        //生成token
        JwtTokenUtil jwtTokenUtil = ContextUtils.getBean(JwtTokenUtil.class);
        Map<String,Object> result = jwtTokenUtil.responseToken(resp,authentication);

        //返回前端
        resp.setContentType("application/json;charset=utf-8");
        PrintWriter out = resp.getWriter();
        out.write(JSON.toJSONString(ResultBody.success(result)));
        out.flush();
        out.close();
    }

    /**
    * @Description: 用户登录清除相关 缓存
    * @author: liv
    * @date  2022.1.14 11:00
    */
    private void clearCache(Authentication authentication){
        String username = authentication.getName();
        //重置错误次数
        SecurityAuthenticationCacheUtil.deleteRetryTimes(username);
    }
}
