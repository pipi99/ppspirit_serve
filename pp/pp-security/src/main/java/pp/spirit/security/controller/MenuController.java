package pp.spirit.security.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;
import pp.spirit.base.base.BaseController;
import pp.spirit.base.base.ResultBody;
import pp.spirit.base.utils.Collection2TreeUtils;
import pp.spirit.security.pojo.Menu;
import pp.spirit.security.pojo.MenuQuery;
import pp.spirit.security.service.MenuService;

import java.util.List;

@RestController
@RequestMapping(value = "/sys/menu")
@Api(tags = "菜单管理")
public class MenuController extends BaseController<Menu,MenuQuery, MenuService> {

    @ApiOperation(value = "查询当前登录用拥有的菜单列表")
    @GetMapping(value="/getCurrentUserMenus")
    public ResultBody getCurrentUserMenus() throws Exception {
        return ResultBody.success(service.getCurrentUserMenus());
    }

    @ApiOperation(value = "查询当前登录用拥有的组件列表-vben-router使用")
    @GetMapping(value="/getCurrentUserMenuComponents")
    public ResultBody getCurrentUserMenuComponents() throws Exception {
        return ResultBody.success(service.getCurrentUserMenuComponents());
    }

    @ApiOperation(value = "分页查询列表-树状结构")
    @PostMapping(value="/pageTreeList")
    public ResultBody pageTreeList(@RequestBody MenuQuery query) throws Exception {
        IPage<Menu> results = service.page(query.getMpPage(),query.getQueryWrapper());
        results.setRecords(Collection2TreeUtils.getTree(results.getRecords(),"menuId","parentId","children"));
        return ResultBody.success(results);
    }

    @ApiOperation(value = "菜单目录-树状结构")
    @PostMapping(value="/dirTreeList")
    public ResultBody dirTreeList( ) throws Exception {
        MenuQuery query = new MenuQuery();
        query.setIsMenu(0);
        List<Menu> results = service.list(query.getQueryWrapper());
        results = Collection2TreeUtils.getTree(results,"menuId","parentId","children");
        return ResultBody.success(results);
    }
}
