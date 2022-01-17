package pp.spirit.security.springsecurity.decision;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;
import org.springframework.stereotype.Component;
import pp.spirit.security.pojo.Permission;
import pp.spirit.security.service.MenuService;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;

/**
 * 本类精准匹配访问资源链接
 */
@Slf4j
@Component
public class UrlFilterInvocationSecurityMetadataSource implements FilterInvocationSecurityMetadataSource {

    @Autowired
    private MenuService menuService;

    @Override
    public Collection<ConfigAttribute> getAttributes(Object object) throws IllegalArgumentException {
        HttpServletRequest request = ((FilterInvocation)object).getRequest();

        //获取当前请求的资源id
        String resourceId = ResourcesAcceptConstantsUtil.getResourceId(request);
        if(StringUtils.isNotEmpty(resourceId)){
            Permission permission = menuService.getPermissionById(Long.parseLong(resourceId));
            //获取当前资源访问所需的权限
            return Lists.newArrayList(new SecurityConfig(permission.getPermission()));
        }else{
            // 获取请求地址
            String requestUrl = ((FilterInvocation) object).getRequestUrl();
            Permission permission  = menuService.getPermissionByRequestUrl(requestUrl);
            //获取当前资源访问所需的权限
            return Lists.newArrayList(new SecurityConfig(permission.getPermission()));
        }
    }

    @Override
    public Collection<ConfigAttribute> getAllConfigAttributes() {
        return null;
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return FilterInvocation.class.isAssignableFrom(aClass);
    }
}
