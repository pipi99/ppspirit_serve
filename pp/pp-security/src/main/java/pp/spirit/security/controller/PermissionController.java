package pp.spirit.security.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import pp.spirit.base.base.BaseController;
import pp.spirit.base.base.ResultBody;
import pp.spirit.security.pojo.OwnerPermission;
import pp.spirit.security.pojo.Permission;
import pp.spirit.security.pojo.PermissionQuery;
import pp.spirit.security.service.OwnerPermissionService;
import pp.spirit.security.service.PermissionService;

import java.util.List;

@RestController
@RequestMapping(value = "/sys/permission")
@Api(tags = "权限管理")
public class PermissionController extends BaseController<Permission,PermissionQuery, PermissionService> {

    @Autowired
    OwnerPermissionService ownerPermissionService;

    @ApiOperation(value = "查询列表-树状结构")
    @PostMapping(value="/treeList")
    public ResultBody pageTreeList() throws Exception {
        return ResultBody.success(ownerPermissionService.treeList());
    }

    @ApiOperation(value = "保存用户、角色、组 - 权限关联关系")
    @PostMapping(value="/saveOwnerPermission")
    public ResultBody saveOwnerPermission(@RequestBody  List<OwnerPermission> ownerPermissionList) throws Exception {
        return ResultBody.success(ownerPermissionService.savePermission(ownerPermissionList));
    }

    @ApiOperation(value = "删除用户、角色、组 - 权限关联关系")
    @DeleteMapping(value="/removeOwnerPermission/{ownerId}")
    public ResultBody removeOwnerPermission(@PathVariable("ownerId") Long ownerId) throws Exception {
        ownerPermissionService.removeAndClearCache(ownerId);
        return ResultBody.success();
    }
}
