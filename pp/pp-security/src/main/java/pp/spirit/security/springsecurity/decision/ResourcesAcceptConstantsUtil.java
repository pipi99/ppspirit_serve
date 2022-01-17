package pp.spirit.security.springsecurity.decision;

import javax.servlet.http.HttpServletRequest;

/**
 * 资源相关常量
 */
public interface ResourcesAcceptConstantsUtil {
    static enum ResourcesAcceptConstants{

        //资源id的参数名称
        RESOURCE_ID("p_s_r_id"),
        //当请求的url 中，，给与本菜单一个所需的 不存在的权限，任何用户 不可访问
        PERMISSION_FOR_NO_RESOURCE_ID("permission_for_no_resource_id_Any_User_Denied"),
        //请求的资源为非授权访问的时候，返回本权限标识
        PERMISSION_FOR_NEED_LOGIN("permission_for_need_login");

        private String value;

        ResourcesAcceptConstants(String value){
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }
    static String getResourceId(HttpServletRequest request){
        return request.getParameter(ResourcesAcceptConstants.RESOURCE_ID.value);
    }
}
