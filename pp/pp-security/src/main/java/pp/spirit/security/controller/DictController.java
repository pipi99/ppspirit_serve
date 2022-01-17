package pp.spirit.security.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pp.spirit.base.base.BaseController;
import pp.spirit.base.base.ResultBody;
import pp.spirit.security.pojo.Dict;
import pp.spirit.security.pojo.DictQuery;
import pp.spirit.security.service.DictService;

import java.util.List;

@RestController
@RequestMapping(value = "/sys/dict")
@Api(tags = "字典管理")
public class DictController extends BaseController<Dict,DictQuery, DictService> {
    @ApiOperation(value = "根据 CODE 获取字典列表")
    @ApiImplicitParam(name = "CODE", value = "字典类型CODE", required = true, dataTypeClass = String.class, paramType = "path",defaultValue = "0")
    @GetMapping(value="/getDictByTypeCode/{code}")
    public ResultBody getDictByTypeCode(@PathVariable("code") String code){
        DictQuery query = new DictQuery();
        query.setDictTypeCode(code);
        List list = service.list(query.getQueryWrapper());
        return ResultBody.success(list);
    }
}
