package pp.spirit.security.springsecurity.decision;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Slf4j
@Component
public class UrlAccessDecisionManager implements AccessDecisionManager {

    @Override
    public void decide(Authentication authentication, Object o, Collection<ConfigAttribute> collection) throws AccessDeniedException, InsufficientAuthenticationException {

        // 当前请求需要的权限
        log.debug("collection:{}", collection);
        // 当前用户所具有的权限
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        log.debug("principal:{} authorities:{}", authentication.getPrincipal().toString(), authorities);

        for (ConfigAttribute configAttribute : collection) {
            // 当前请求需要的权限
            String needPermission = configAttribute.getAttribute();
            //如果是非授权可访问的菜单
            if (ResourcesAcceptConstantsUtil.ResourcesAcceptConstants.PERMISSION_FOR_NEED_LOGIN.getValue().equals(needPermission)) {
                if (authentication instanceof AnonymousAuthenticationToken) {
                    throw new BadCredentialsException("Not logged in!!");
                } else {
                    return;
                }
            }
            // 当前用户所具有的权限
            for (GrantedAuthority grantedAuthority : authorities) {
                // 包含其中一个权限即可访问
                if (grantedAuthority.getAuthority().equals(needPermission)) {
                    return;
                }
            }
        }
        throw new AccessDeniedException("SimpleGrantedAuthority!!");
    }

    @Override
    public boolean supports(ConfigAttribute configAttribute) {
        return true;
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return true;
    }
}
