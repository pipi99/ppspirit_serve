package pp.spirit.security.springsecurity.handler;

import com.alibaba.fastjson.JSON;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import pp.spirit.base.base.ResultBody;
import pp.spirit.base.base.ResultCodeMessage;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class JwtAccessDeniedHandler implements AccessDeniedHandler {
    @Override
    public void handle(HttpServletRequest httpServletRequest, HttpServletResponse response, AccessDeniedException e) throws IOException, ServletException {
        String json =  JSON.toJSONString(ResultBody.result(ResultCodeMessage.FORBIDDEN,"权限不足"));
        response.setContentType("text/json;charset=utf-8");
        response.getWriter().write(json);
    }
}
