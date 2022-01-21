package pp.spirit.security.pojo;


import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.validator.constraints.Length;
import pp.spirit.base.base.BaseBean;
import pp.spirit.base.incrementer.IdGenerator;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.util.List;

@Data
@Entity
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="菜单实体", description="菜单实体")
public class Menu extends BaseBean<Menu> {

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
    @ApiModelProperty(value = "菜单名称")
    private String menuName;

    /**
     * 上级菜单ID
     **/
    @ApiModelProperty(value = "上级菜单ID")
    private Long  parentId;

    /**
     * 菜单排序
     **/
    @ApiModelProperty(value = "排序号")
    private Integer sort;

    /**
     * 是否菜单还是目录
     **/
    @ApiModelProperty(value = "菜单或者目录  1 菜单  0 目录")
    private Integer isMenu;

    /**
     * 是否启用 1是 0否
     **/
    @ApiModelProperty(value = "是否启用 1是 0否")
    private Integer isEnabled;

    @ApiModelProperty(value = "是否隐藏 1是 0否")
    private Integer isHidden;

    /**
     * 菜单链接
     **/
    @Length(max = 1000)
    @ApiModelProperty(value = "访问地址")
    private String path;

    /**
     * 菜单图标
     **/
    @ApiModelProperty(value = "菜单图标")
    @Length(max = 200)
    private String icon;

    /**
     * 是否框架内打开页面  1 是 0否
     **/
    @ApiModelProperty(value = "是否框架内打开页面  1 是 0否")
    private Integer inFrame;

    /**
     * 菜单对象（组件）
     **/
    @Length(max = 1000)
    @ApiModelProperty(value = "路由组件")
    private String target;

    /**
     * 是否组件
     **/
    @ApiModelProperty(value = "是否组件 1 是 0否")
    private Integer isComponent;

    /**
     * 菜单说明
     **/
    @ApiModelProperty(value = "菜单说明")
    private String description;

    @OneToMany(cascade=CascadeType.ALL,fetch = FetchType.EAGER)
    @JoinColumn(name = "resourceId") //表示对应子表的关联外键，如果不使用这个注解则需要创建中间表
    @TableField(exist = false)
    @JsonIgnore
    @ApiModelProperty(value = "菜单权限")
    private List<Permission> permission;

    /**
     * 访问控制类型：0：无需登录或授权访问 1： 登录后访问。 2：授权后访问。
     **/
    @ApiModelProperty(value = "访问控制类型：0：无需登录或授权访问 1： 登录后访问。 2：授权后访问。")
    private Integer accessCtrl;

    @TableField(exist = false)
    @Transient
    @ApiModelProperty(value = "下级菜单")
    private List<Menu> children;

    //菜单下的按钮
    @OneToMany(targetEntity = MenuActions.class,fetch = FetchType.EAGER)
    @JoinColumn(name = "menuId") //表示对应子表的关联外键，如果不使用这个注解则需要创建中间表
    @TableField(exist = false)
    @Fetch(FetchMode.SUBSELECT)
    @ApiModelProperty(value = "菜单下的按钮")
    private List<MenuActions> menuActions;

    @Override
    public void valid(Menu menu){
    }
}
