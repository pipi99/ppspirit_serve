package pp.spirit.security.controller;

import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pp.spirit.base.base.BaseController;
import pp.spirit.security.pojo.MenuAll;
import pp.spirit.security.pojo.MenuAllQuery;
import pp.spirit.security.service.MenuAllService;

@RestController
@RequestMapping(value = "/sys/menuall")
@Api(tags = "菜单管理")
public class MenuAllController extends BaseController<MenuAll, MenuAllQuery, MenuAllService> {
}
