package pp.spirit.security.controller;

import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pp.spirit.base.base.BaseController;
import pp.spirit.security.pojo.Group;
import pp.spirit.security.pojo.GroupQuery;
import pp.spirit.security.service.GroupService;

@RestController
@RequestMapping(value = "/sys/group")
@Api(tags = "组管理")
public class GroupController extends BaseController<Group,GroupQuery, GroupService> {
}
