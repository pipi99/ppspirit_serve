package pp.spirit.security.pojo;


import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.validator.constraints.Length;
import pp.spirit.base.base.BaseBean;
import pp.spirit.base.incrementer.IdGenerator;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotEmpty;

@Data
@Entity
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="MenuAll", description="系统中所有的可用菜单，开发的时候录入，用户使用的为 menu中的菜单，可以从本库中选择需要使用的系统既定菜单！")
public class MenuAll extends BaseBean<MenuAll> {
    /**
     * 主键
     * Nullable:  false
     */
    @Id
    @TableId
    @GeneratedValue(generator = IdGenerator.GEN_NAME,strategy = GenerationType.SEQUENCE)
    @GenericGenerator(name = IdGenerator.GEN_NAME, strategy = IdGenerator.GEN_CLASS_NAME)
    @ApiModelProperty(value = "主键")
    private Long menuId;

    /**
     * 菜单名称
     **/
    @NotEmpty(message = "菜单名称不能为空")
    @Length(max = 100)
    private String menuName;

    /**
     * 菜单链接
     **/
    @NotEmpty(message = "组件地址不能为空")
    @Length(max = 1000)
    private String path;

    /**
     * 菜单说明
     **/
    private String description;
}
