package pp.spirit.io.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.*;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pp.spirit.base.base.BaseController;
import pp.spirit.base.base.ResultBody;
import pp.spirit.base.validate.annotation.ValidResult;
import pp.spirit.io.pojo.PPFile;
import pp.spirit.io.pojo.PPFileQuery;
import pp.spirit.io.service.FileService;
import pp.spirit.io.util.FileUpDownUtil;

import javax.validation.Valid;
import java.io.IOException;
import java.util.List;

/**
 * @author LiV
 * @Title:
 * @Package com.liv.sp.controller
 * @Description: 控制器
 * @date 2020.12.11  10:51
 * @email 453826286@qq.com
 */
@RestController
@RequestMapping(value = "/file")
@Api(tags = "文件管理控制器")
public class FileController extends BaseController<PPFile,PPFileQuery,FileService> {

    @ApiOperation(value = "根据ID查询")
    @ApiImplicitParam(name = "id", value = "当前登录ID", required = true, dataTypeClass = String.class, paramType = "query",defaultValue = "1")
    @GetMapping(value="/getById")
    public ResultBody getById(@RequestParam("id") Long id) throws Exception {
        return ResultBody.success(service.getById(id));
    }

    @ApiOperation(value = "查询列表", notes="查询列表")
    @PostMapping(value="/list")
    public ResultBody list(@RequestBody PPFileQuery query) throws Exception {
        return ResultBody.success(service.list(query.getQueryWrapper()));
    }

    @ApiOperation(value = "分页查询列表", notes="分页查询列表")
    @PostMapping(value="/pagelist")
    public ResultBody pagelist(@RequestBody PPFileQuery query) throws Exception {
        IPage<PPFile> pageList = service.page(query.getMpPage(),query.getQueryWrapper());
        return ResultBody.success(pageList);
    }

    @ApiOperation(value = "新增", notes="新增")
    @PostMapping(value="/save",headers = "content-type=multipart/form-data")
    @ApiImplicitParam(name = "file", value = "文件上传", required = true, dataTypeClass = MultipartFile.class, paramType = "formData")
    @ValidResult
    public ResultBody save(@RequestPart @RequestParam(value = "file", required = true) MultipartFile file) throws Exception {
        PPFile f = FileUpDownUtil.upload(file);
        return ResultBody.success(f);
    }

    @ApiOperation(value = "批量新增", notes="批量新增")
    @PostMapping(value="/saveBatch",headers = "content-type=multipart/form-data")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "file", value = "文件上传", required = true, dataTypeClass = MultipartFile.class, paramType = "formData"),
        @ApiImplicitParam(name = "file1(提交时修改成 file )", value = "文件上传1", required = true, dataTypeClass = MultipartFile.class, paramType = "formData")
    })
    @ValidResult
    public ResultBody saveBatch(@RequestPart @RequestParam(value = "file", required = true) List<MultipartFile> files) throws Exception {
        List<PPFile> list =  FileUpDownUtil.batchUpload(files);
        return ResultBody.success(list);
    }

    @ApiOperation(value = "更新", notes="更新,根据主键id更新")
    @PutMapping(value="/update")
    @ValidResult
    public ResultBody update(@RequestBody(required = true) @Valid PPFile d, BindingResult result) {
        service.updateById(d);
        return ResultBody.success("修改成功");
    }

    @ApiOperation(value = "删除", notes="删除,根据主键id删除")
    @DeleteMapping(value="/remove")
    public ResultBody remove(@RequestParam(value = "fileId", required = true)  String fileId) throws Exception {
        FileUpDownUtil.remove(fileId);
        return ResultBody.success("删除成功");
    }

    @ApiOperation(value = "批量删除", notes="删除,根据主键id删除")
    @PostMapping(value="/removeBatch")
    public ResultBody removeBatch(@RequestBody List<String> fileIds) throws Exception {
        FileUpDownUtil.removeBatch(fileIds);
        return ResultBody.success("删除成功");
    }

    @ApiOperation(value = "下载", notes="下载")
    @GetMapping(value="/download")
    @ApiImplicitParam(name = "fileId", value = "文件编码", required = true, dataTypeClass = String.class, paramType = "query")
    public void download(@RequestParam(value = "fileId", required = true) String fileId) throws IOException {
        FileUpDownUtil.download(fileId);
    }

    @ApiOperation(value = "预览", notes="预览")
    @GetMapping(value="/preview")
    @ApiImplicitParam(name = "fileId", value = "文件编码", required = true, dataTypeClass = String.class, paramType = "query")
    public void previewImg(@RequestParam(value = "fileId", required = true) String fileId) throws IOException {
        FileUpDownUtil.preview(fileId);
    }

    @ApiOperation(value = "预览多媒体", notes="预览多媒体")
    @GetMapping(value="/previewMedia")
    @ApiImplicitParam(name = "fileId", value = "文件编码", required = true, dataTypeClass = String.class, paramType = "query")
    public void previewMedia(@RequestParam(value = "fileId", required = true) String fileId) throws Exception {
        FileUpDownUtil.previewMedia(fileId);
    }

    @ApiOperation(value = "获取文件", notes="获取文件")
    @GetMapping(value="/getFile")
    @ApiImplicitParam(name = "fileId", value = "文件编码", required = true, dataTypeClass = String.class, paramType = "query")
    public ResultBody getFile(@RequestParam(value = "fileId", required = true) String fileId) throws IOException {
        return ResultBody.success(FileUpDownUtil.getFile(fileId));
    }
}
