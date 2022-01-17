package pp.spirit.security.pojo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.GenericGenerator;
import pp.spirit.base.base.BaseBean;
import pp.spirit.base.incrementer.IdGenerator;

import javax.persistence.*;

/**
 * @author LiV
 * @Title:
 * @Package com.liv.api.auth.dao.datamodel
 * @Description: 操作操作
 * @date 2020.5.25  17:45
 * @email 453826286@qq.com
 */

@Data
@Entity
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "MenuActions", description = "按钮操作")
public class MenuActions  extends BaseBean<MenuActions> {
    /**
     * 主键
     *
     * Nullable:  false
     */
    @Id
    @TableId
    @GeneratedValue(generator = IdGenerator.GEN_NAME,strategy = GenerationType.SEQUENCE)
    @GenericGenerator(name = IdGenerator.GEN_NAME, strategy = IdGenerator.GEN_CLASS_NAME)
    @ApiModelProperty(value = "主键")
    private Long actionId;
    /**
     * 上级ID
     **/
    private Long  menuId;
    /**
     * 操作代号
     **/
    private String actionCode;
    /**
     * 操作名称
     **/
    private String actionName;


    @ManyToOne
    @JoinColumn(name = "menuId",insertable = false,updatable = false) //表示对应子表的关联外键，如果不使用这个注解则需要创建中间表
    @TableField(exist = false)
    @JsonIgnore
    private Menu menu;
}
