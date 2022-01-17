package pp.spirit.security.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;
import pp.spirit.base.base.BaseController;
import pp.spirit.base.base.ResultBody;
import pp.spirit.security.pojo.User;
import pp.spirit.security.pojo.UserQuery;
import pp.spirit.security.service.UserService;

@RestController
@RequestMapping(value = "/sys/user")
@Api(tags = "用户管理")
public class UserController extends BaseController<User,UserQuery, UserService> {

    @ApiOperation(value = "初始化密码", notes="初始化密码")
    @ApiImplicitParam(name = "userId", value = " 查询用户用户ID", required = true, dataTypeClass = String.class,paramType = "path",defaultValue = "1")
    @GetMapping(value="/resetPwd/{userId}")
    public ResultBody resetPwd(@PathVariable("userId") Long userId) throws Exception {
        service.resetPwd(userId);
        return ResultBody.success();
    }

    @ApiOperation(value = "分页查询列表JPA-支持联合查询")
    @PostMapping(value="/pagelistJpa")
    public ResultBody pagelistJpa(@RequestBody UserQuery query) throws Exception {
        Page<User> results = service.getRepository().findAllReturnMybatisPlus(query);
        //清空密码，返回前台
        results.getRecords().stream().forEach(user -> user.setPassword(null));
        return ResultBody.success(results);
    }

    @ApiOperation(value = "用户锁定状态")
    @PostMapping(value="/lock")
    public ResultBody lock(@RequestBody User user) throws Exception {
        service.doLock(user);
        return ResultBody.success();
    }

    @ApiOperation(value = "用户可用状态")
    @PostMapping(value="/enabled")
    public ResultBody enabled(@RequestBody User user) throws Exception {
        service.doEnabled(user);
        return ResultBody.success();
    }
}
