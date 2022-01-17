package pp.spirit.security.pojo;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.validator.constraints.Length;
import pp.spirit.base.base.BaseBean;
import pp.spirit.base.incrementer.IdGenerator;
import pp.spirit.base.utils.ContextUtils;
import pp.spirit.base.validate.ValidException;
import pp.spirit.security.service.DictService;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.util.List;

/**
 * @author LiV
 * @Title:
 * @Package com.liv.api.auth.pojo
 * @Description: 字典
 * @date 2021.3.8  17:09
 * @email 453826286@qq.com
 */
@Data
@Entity
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="Dict", description="字典实体")
public class Dict extends BaseBean<Dict> implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @TableId
    @GeneratedValue(generator = IdGenerator.GEN_NAME, strategy = GenerationType.SEQUENCE)
    @GenericGenerator(name = IdGenerator.GEN_NAME, strategy = IdGenerator.GEN_CLASS_NAME)
    @ApiModelProperty(value = "主键")
    private Long dictId;

    @ApiModelProperty(value = "字典类型")
    @NotEmpty(message = "字典类型不能为空")
    @Length(max = 100)
    private String dictTypeCode;

    @ApiModelProperty(value = "字典名称")
    @NotEmpty(message = "字典名称不能为空")
    @Length(max = 100)
    private String dictName;

    @ApiModelProperty(value = "字典编号")
    @NotEmpty(message = "字典编号不能为空")
    @Length(max = 100)
    private String dictCode;

    @Override
    public void valid(Dict dict){
        DictService service = ContextUtils.getBean("dictService", DictService.class);

        QueryWrapper<Dict> queryWrapper = new QueryWrapper<>();
        if (dict.getDictId() != null) {
            queryWrapper.ne("DICT_ID", dict.getDictId());
        }
        queryWrapper.eq("DICT_TYPE_CODE", dict.getDictTypeCode());
        queryWrapper.and(wrapper -> wrapper.eq("DICT_CODE", dict.getDictCode()).or().eq("DICT_NAME", dict.getDictName()));
        List list = service.list(queryWrapper);

        //执行校验
        if (list != null && list.size() > 0) {
            throw new ValidException("同一类型下的字典值或者字典名称不能相同哦！");
        }
    }
}
