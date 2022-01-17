package pp.spirit.security.springsecurity.decision;

/**系统资源访问控制类型*/
public enum ResourcesAcceptTypes {

    NEED_LOGIN("need_login"),// 登录访问
    NEED_AUTHORIZE("need_authorize"),// 授权访问
    ANONYMOUS("anonymous");// 匿名访问

    private String value;

    ResourcesAcceptTypes(String value){
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
