前端框架：[https://vvbin.cn/doc-next/](https://vvbin.cn/doc-next/)  
后端框架：spring-security  
通用权限：用户、角色、用户组、用户组角色<——>权限<——>资源

## 一、说明
菜单表分为两张 MENU_ALL ，MENU
> MENU_ALL：开发者录入所有系统可用菜单，与具体使用者无关,供超级管理员分配给系统管理员。    
> MENU：由系统管理员管理，提供给系统用户使用。

## 二、鉴权
`401`:未登录  
`403`:无权限  

1、token / refreshToken方式 ,正常请求的过滤只判断 token。当token失效，如果直接到后台，会提示401 （未登录状态），前端应根据refreshToken进行刷新token操作。  
> 浏览器的token只会在访问后台请求的时候使用。  
> 路由的跳转判断token使用 userState中token,token失效操作判断在checkStatus.ts中

2、refreshToken 的刷新会同时更新 token / refreshToken 的过期时间。  
3、remember-me 依赖 localStorage 和 sessionStory 实现，由前端处理  
4、密码登录采用AES+RSA加密，RSA密钥对可用缓存时间为1分钟。  

### 2.1 权限拦截
1、后台根据用户的权限，返回可以访问的菜单列表。后台根据开发时指定的权限、角色注解或者过滤器，判断用户是否可以访问链接。  
2、前端vben框架采用动态路由（vben的路由模式采用`BACK`），根据后台鉴权结果菜单列表，动态注册路由。
