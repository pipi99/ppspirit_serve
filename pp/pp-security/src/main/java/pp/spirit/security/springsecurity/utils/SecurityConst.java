package pp.spirit.security.springsecurity.utils;

public interface SecurityConst {

    //===============菜单权限控制============
    /*开放链接*/
    Integer MENU_ANONYMOUS = 0;
    /*用户登录访问*/
    Integer MENU_LOGIN = 1;
    /*授权访问*/
    Integer MENU_AUTH = 2;
    //===============菜单权限控制============

    //===============系统默认角色============
    //超级管理员
    String ROLE_SUPER_ADMINISTRATOR = "ROLE_SUPER_ADMINISTRATOR";
    //管理员
    String ROLE_ADMINISTRATOR = "ROLE_ADMINISTRATOR";
    //匿名
    String ROLE_ANONYMOUS = "ROLE_ANONYMOUS";
    //访客
    String ROLE_GUEST = "ROLE_GUEST";
    //===============系统默认角色============

    //===============权限类型============
    //菜单类型
    String PERMISSION_TYPE_MENU = "menu";
    //按钮类型
    String PERMISSION_TYPE_ACTION = "action";
    //按钮类型
    String PERMISSION_TYPE_CUSTOMIZE = "customize";
    //===============权限类型============

}
