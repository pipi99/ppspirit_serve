package pp.spirit.security.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pp.spirit.base.base.BaseController;
import pp.spirit.base.base.ResultBody;
import pp.spirit.security.pojo.DictQuery;
import pp.spirit.security.pojo.DictType;
import pp.spirit.security.pojo.DictTypeQuery;
import pp.spirit.security.service.DictService;
import pp.spirit.security.service.DictTypeService;

import java.util.List;

@RestController
@RequestMapping(value = "/sys/dicttype")
@Api(tags = "字典类型管理")
public class DictTypeController extends BaseController<DictType, DictTypeQuery, DictTypeService> {

    @Autowired
    private DictService dictService;


    @ApiOperation(value = "根据ID删除")
    @ApiImplicitParam(name = "id", value = "主键", required = true, dataTypeClass = String.class, paramType = "path",defaultValue = "0")
    @DeleteMapping(value="/removeById/{id}")
    public ResultBody removeById(@PathVariable("id") Long id){
        DictType dictType = service.getById(id);

        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("DICT_TYPE_CODE",dictType.getDictTypeCode());
        List list = dictService.list(queryWrapper);
        if(list.size()>0){
            throw new RuntimeException("类型下有数据，请先删除字典数据");
        }
        service.removeById(id);
        return ResultBody.success("删除成功");
    }
}
