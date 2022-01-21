package pp.spirit.security.pojo;


import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.validator.constraints.Length;
import pp.spirit.base.base.BaseBean;
import pp.spirit.base.incrementer.IdGenerator;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.util.List;

/**
* @Description: 组织机构实体
* @author: liv
* @date  2021.12.15 09:21
*/
@Data
@Entity
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="组织机构", description="Organ")
public class Organ extends BaseBean<Organ> implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @TableId
    @GeneratedValue(generator = IdGenerator.GEN_NAME,strategy = GenerationType.SEQUENCE)
    @GenericGenerator(name = IdGenerator.GEN_NAME, strategy = IdGenerator.GEN_CLASS_NAME)
    @ApiModelProperty(value = "主键")
    private Long organId;

    @ApiModelProperty(value = "机构名称")
    @NotEmpty(message = "机构名称不能为空")
    private String organName;

    @ApiModelProperty(value = "上级机构ID")
    private Long parentId;

    @ApiModelProperty(value = "机构级别")
    @NotEmpty(message = "机构级别不能为空")
    private String organLevel;

    @ApiModelProperty(value = "机构类型")
    @NotEmpty(message = "机构类型不能为空")
    private String organType;

    @ApiModelProperty(value = "图标")
    @Length(max = 200)
    private String icon;

    /**
     * 菜单排序
     **/
    @ApiModelProperty(value = "排序号")
    private Integer sort;

    @ApiModelProperty(value = "机构描述")
    @Length(max = 200)
    private String description;

    @ApiModelProperty(value = "是否真实机构 1 是 0否，默认是，人员不能属于非真实机构")
    private Integer actual;

    @ApiModelProperty(value = "是否启用 1 是 0否")
    private Integer enabled;

    @OneToMany(cascade=CascadeType.ALL,fetch = FetchType.EAGER)
    @JoinColumn(name = "orgId") //表示对应子表的关联外键，如果不使用这个注解则需要创建中间表
    @TableField(exist = false)
    @JsonIgnore
    private List<User> users;

    @TableField(exist = false)
    @Transient
    private List<Organ> children;
}
