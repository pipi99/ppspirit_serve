package pp.spirit.security.controller;

import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pp.spirit.base.base.BaseController;
import pp.spirit.security.pojo.Role;
import pp.spirit.security.pojo.RoleQuery;
import pp.spirit.security.service.RoleService;

@RestController
@RequestMapping(value = "/sys/role")
@Api(tags = "角色管理")
public class RoleController extends BaseController<Role,RoleQuery, RoleService> {
}
