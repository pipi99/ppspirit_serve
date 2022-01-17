package pp.spirit.security.springsecurity.handler;

import com.alibaba.fastjson.JSON;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import pp.spirit.base.base.ResultBody;
import pp.spirit.base.utils.ContextUtils;
import pp.spirit.security.springsecurity.utils.JwtTokenUtil;
import pp.spirit.security.springsecurity.utils.SecurityAuthenticationCacheUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
/**
 * 退出成功处理
 */
public class LogoutSuccessHandler implements org.springframework.security.web.authentication.logout.LogoutSuccessHandler{
    @Override
    public void onLogoutSuccess(HttpServletRequest req , HttpServletResponse resp, Authentication authentication) throws IOException, ServletException {
        //TODO 清除用户登录信息
        JwtTokenUtil jwtTokenUtil = ContextUtils.getBean(JwtTokenUtil.class);
        //用户携带的token
        String token = jwtTokenUtil.getRequestToken(req);
        /**
         * jwtId 代表 refreshToken
         */
        if(token!=null) {
            String jwtId = jwtTokenUtil.getJwtIdFromToken(token);
            SecurityAuthenticationCacheUtil.deleteToken(token);
            SecurityAuthenticationCacheUtil.deleteAuthentication(jwtId);
            SecurityContextHolder.clearContext();
        }

        resp.setContentType("application/json;charset=utf-8");
        PrintWriter out = resp.getWriter();
        out.write(JSON.toJSONString(ResultBody.success("注销登录成功")));
        out.flush();
        out.close();
    }
}
