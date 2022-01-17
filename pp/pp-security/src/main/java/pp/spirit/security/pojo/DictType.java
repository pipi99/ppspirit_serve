package pp.spirit.security.pojo;

import com.baomidou.mybatisplus.annotation.TableField;
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
import pp.spirit.security.service.DictTypeService;

import javax.persistence.*;
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
@ApiModel(value="DictType", description="字典类型实体")
public class DictType extends BaseBean<DictType> implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @TableId
    @GeneratedValue(generator = IdGenerator.GEN_NAME,strategy = GenerationType.SEQUENCE)
    @GenericGenerator(name = IdGenerator.GEN_NAME, strategy = IdGenerator.GEN_CLASS_NAME)
    @ApiModelProperty(value = "主键")
    private Long dictTypeId;

    @ApiModelProperty(value = "字典类型编号")
    @NotEmpty(message = "字典类型编号不能为空")
    @Length(max = 100)
    private String dictTypeCode;

    @ApiModelProperty(value = "字典类型名称")
    @NotEmpty(message = "字典类型名称不能为空")
    @Length(max = 100)
    private String dictTypeName;

    @ApiModelProperty(value = "字典类型说明")
    @Length(max = 100)
    private String description;

    @TableField(exist = false)
    @Transient
    @JoinColumn(name = "dict_type_code", referencedColumnName = "dict_type_code")
    @OneToMany(fetch=FetchType.EAGER)
    List<Dict> dictList;


    @Override
    public void valid(DictType dictType){
        DictTypeService service = ContextUtils.getBean("dictTypeService", DictTypeService.class);

        QueryWrapper<DictType> queryWrapper = new QueryWrapper<>();
        if(dictType.getDictTypeId()!=null){
            queryWrapper.ne("DICT_TYPE_ID",dictType.getDictTypeId());
        }
        queryWrapper.and(wrapper ->wrapper.eq("DICT_TYPE_CODE",dictType.getDictTypeCode()).or().eq("DICT_TYPE_NAME",dictType.getDictTypeName()));
        List list = service.list(queryWrapper);

        //执行校验
        if(list!=null&&list.size()>0){
            throw new ValidException("字典类型名称或值不能重复！");
        }
    }
}
