package pp.spirit.security.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pp.spirit.base.base.BaseController;
import pp.spirit.base.base.ResultBody;
import pp.spirit.base.utils.Collection2TreeUtils;
import pp.spirit.security.pojo.Organ;
import pp.spirit.security.pojo.OrganQuery;
import pp.spirit.security.service.OrganService;

import java.util.List;

@RestController
@RequestMapping(value = "/sys/organ")
@Api(tags = "机构管理")
public class OrganController extends BaseController<Organ,OrganQuery, OrganService> {

    @ApiOperation(value = "分页查询列表-树状结构")
    @PostMapping(value="/pageTreeList")
    public ResultBody pageTreeList(@RequestBody OrganQuery query) throws Exception {
        IPage<Organ> results = service.page(query.getMpPage(),query.getQueryWrapper());
        results.setRecords(Collection2TreeUtils.getTree(results.getRecords(),"organId","parentId","children"));
        return ResultBody.success(results);
    }

    @ApiOperation(value = "查询列表-树状结构")
    @PostMapping(value="/treeList")
    public ResultBody treeList(@RequestBody OrganQuery query) throws Exception {
        List<Organ> results = service.list(query.getQueryWrapper());
        return ResultBody.success(Collection2TreeUtils.getTree(results,"organId","parentId","children"));
    }
}
