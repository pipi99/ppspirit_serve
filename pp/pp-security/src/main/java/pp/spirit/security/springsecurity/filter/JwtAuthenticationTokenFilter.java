package pp.spirit.security.springsecurity.filter;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;
import pp.spirit.base.utils.ContextUtils;
import pp.spirit.security.springsecurity.utils.JwtTokenUtil;
import pp.spirit.security.springsecurity.utils.SecurityAuthenticationCacheUtil;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * jwtToken过滤器，校验token
 * 判断用户是否已经登录
 */
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {
        JwtTokenUtil jwtTokenUtil = ContextUtils.getBean(JwtTokenUtil.class);

        //用户携带的token
        String token = jwtTokenUtil.getRequestToken(httpServletRequest);

        String jwtId = null;
        /**
         * jwtId 代表 refreshToken
         */
        //token失效时，jwtId == null
        //1 重校验
        if(token!=null&&((jwtId = jwtTokenUtil.getJwtIdFromToken(token))!=null)){
            Authentication authentication = SecurityAuthenticationCacheUtil.getAuthentication(jwtId);
            // 校验token是否有效，无效则退出
            // 2 重校验
            if(authentication!=null&&jwtTokenUtil.validateToken(token, ((UserDetails)authentication.getPrincipal()).getUsername())){
                //每次请求重新绑定一下登录信息到 **当前线程**
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }else{
                SecurityContextHolder.clearContext();
            }
        }else {
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(httpServletRequest, httpServletResponse);
        return;
    }
}
