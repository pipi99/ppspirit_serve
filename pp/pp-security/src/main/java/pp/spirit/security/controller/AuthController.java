package pp.spirit.security.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import pp.spirit.base.base.ResultBody;
import pp.spirit.security.pojo.Permission;
import pp.spirit.security.pojo.User;
import pp.spirit.security.service.UserService;
import pp.spirit.security.springsecurity.utils.SecurityUtil;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;



@Slf4j
@RestController
@RequestMapping(value = "/sys/auth")
@Api(tags = "AUTH API组件控制器")
public class AuthController {

    @Autowired
    private UserService userService;



    @ApiOperation(value = "用户注册", notes="注册用户")
    @GetMapping(value="/reg")
    public ResultBody reg(String username,String alias,String password) throws Exception {
        if(userService.findByUserName(username) == null){
            User user = new User();
            user.setUserName(username);
            user.setAlias(alias);
            user.setPassword(password);
            return ResultBody.success(userService.reg(user));
        }
        return ResultBody.fail("用户名称重复");
    }

    
    @ApiOperation(value = "获取当前登录用户", notes="获取当前登录用户")
    @GetMapping("/signedUser")
    public ResultBody signedUser() throws IOException {
        return ResultBody.success(userService.signedUser());
    }

    @ApiOperation(value = "获取当前登录用户", notes="获取当前登录用户")
    @GetMapping("/getPermCode")
    public ResultBody getPermCode() throws IOException {
        return ResultBody.success(SecurityUtil.getCurrentUserPermissions().stream().map(Permission::getPermission));
    }

    /**
     * @description
     * @param   
     * @return 
     * @author liv
     * @date   2021.12.15 09:17
     */
    @ApiOperation(value = "用户退出", notes="用户退出")
    @GetMapping("/logout")
    public void logout(HttpServletResponse response) throws IOException {
        response.sendRedirect("/signout");
    }

    @PostMapping("/resetPwd")
    @ApiOperation(value = "修改密码", notes="修改密码")
    public ResultBody resetPwd(@RequestParam String oldPwd, @RequestParam String newPwd){
        boolean result = userService.resetPwd( oldPwd,  newPwd);
        return result?ResultBody.success("密码修改成功！"):ResultBody.fail("密码修改失败！");
    }
}
