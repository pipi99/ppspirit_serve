package pp.spirit.base.base;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import pp.spirit.base.validate.annotation.ValidResult;

import javax.validation.Valid;
import java.util.List;

@SuppressWarnings("ALL")
public abstract class AbstractBaseController<T extends BaseBean,Q extends BaseQuery,S extends IBaseService> {

    /*service 自动注入 */
    @Autowired
    protected S service;

    @ApiOperation(value = "新增", notes="新增")
    @PostMapping(value="/save")
    @ValidResult
    public ResultBody save(@RequestBody(required = true) @Valid T u, BindingResult result) {
        return ResultBody.success(service.save(u));
    }

    @ApiOperation(value = "根据ID更新")
    @PutMapping(value="/update")
    @ValidResult
    public ResultBody update(@RequestBody(required = true)@Valid T u, BindingResult result) throws Exception {
        service.updateById(u);
        return ResultBody.success("更新成功");
    }

    @ApiOperation(value = "根据ID删除")
    @ApiImplicitParam(name = "id", value = "主键", required = true, dataTypeClass = String.class, paramType = "path",defaultValue = "0")
    @DeleteMapping(value="/removeById/{id}")
    public ResultBody removeById(@PathVariable("id") Long id){
        service.removeById(id);
        return ResultBody.success("删除成功");
    }

    @ApiOperation(value = "根据ID删除JPA-支持联合删除")
    @ApiImplicitParam(name = "id", value = "主键", required = true, dataTypeClass = String.class, paramType = "path",defaultValue = "0")
    @DeleteMapping(value="/removeByIdJpa/{id}")
    public ResultBody removeByIdJpa(@PathVariable("id") Long id){
        service.getRepository().findById(id).ifPresent(
                x->service.getRepository().deleteById(id)
        );
        return ResultBody.success("删除成功");
    }

    @ApiOperation(value = "根据ID批量删除")
    @ApiImplicitParam(name = "id", value = "主键", required = true, dataTypeClass = String.class, paramType = "path",defaultValue = "0")
    @DeleteMapping(value="/removeByIds")
    public ResultBody removeByIds(@RequestBody List<Long> ids){
        service.removeByIds(ids);
        return ResultBody.success("删除成功");
    }

    @ApiOperation(value = "根据ID批量删除JPA-支持联合删除")
    @ApiImplicitParam(name = "id", value = "主键", required = true, dataTypeClass = String.class, paramType = "path",defaultValue = "0")
    @DeleteMapping(value="/removeByIdsJpa")
    public ResultBody removeByIdsJpa(@RequestBody List<Long> ids){
        service.getRepository().deleteAllByIdInBatch(ids);
        return ResultBody.success("删除成功");
    }

    @ApiOperation(value = "根据ID查询")
    @ApiImplicitParam(name = "id", value = "主键", required = true, dataTypeClass = String.class, paramType = "path",defaultValue = "0")
    @GetMapping(value="/getById/{id}")
    public ResultBody getById(@PathVariable("id") Long id) throws Exception {
        return ResultBody.success(service.getById(id));
    }

    @ApiOperation(value = "根据ID查询JPA-支持联合查询")
    @ApiImplicitParam(name = "id", value = "主键", required = true, dataTypeClass = String.class, paramType = "path",defaultValue = "0")
    @GetMapping(value="/getByIdJpa/{id}")
    public ResultBody getByIdJpa(@PathVariable("id") Long id) throws Exception {
        return ResultBody.success(service.getRepository().findById(id).orElse(null));
    }

    @ApiOperation(value = "分页查询列表")
    @PostMapping(value="/pagelist")
    public ResultBody pagelist(@RequestBody Q query) throws Exception {
        IPage<T> results = service.page(query.getMpPage(),query.getQueryWrapper());
        return ResultBody.success(results);
    }

    @ApiOperation(value = "分页查询列表JPA-支持联合查询")
    @PostMapping(value="/pagelistJpa")
    public ResultBody pagelistJpa(@RequestBody Q query) throws Exception {
        Page<T> results = service.getRepository().findAllReturnMybatisPlus(query);
        return ResultBody.success(results);
    }

    @ApiOperation(value = "查询列表")
    @PostMapping(value="/list")
    public ResultBody list(@RequestBody Q query) throws Exception {
        return ResultBody.success(service.list(query.getQueryWrapper()));
    }

    @ApiOperation(value = "查询列表JPA-支持联合查询")
    @PostMapping(value="/listJpa")
    public ResultBody listJpa(@RequestBody Q query) throws Exception {
        return ResultBody.success(service.getRepository().findAll(query.getSpecification()));
    }
}
