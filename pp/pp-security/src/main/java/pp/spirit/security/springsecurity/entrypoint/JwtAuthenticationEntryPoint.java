package pp.spirit.security.springsecurity.entrypoint;

import com.alibaba.fastjson.JSON;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import pp.spirit.base.base.ResultBody;
import pp.spirit.base.base.ResultCodeMessage;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException e) throws IOException, ServletException {
        // 使用fastjson
        String json =  JSON.toJSONString(ResultBody.result(ResultCodeMessage.UNAUTHORIZED,"您未登录系统"));
        // 指定响应格式是json
        response.setContentType("text/json;charset=utf-8");
        response.getWriter().write(json);
    }
}