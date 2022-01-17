package pp.spirit.security.pojo;


import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import pp.spirit.base.base.BaseBean;
import pp.spirit.base.incrementer.IdGenerator;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "OwnerPermission", description = "用户、角色、权限关联关系表")
public class OwnerPermission extends BaseBean<OwnerPermission> implements Serializable {
    private static final long serialVersionUID = 1L;
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
    private Long id;

    //用户、角色、用户组
    private Long ownerId;
    //权限ID
    private Long permissionId;

    @ManyToOne
    @JoinColumn(name = "ownerId",insertable = false,updatable = false) //表示对应子表的关联外键，如果不使用这个注解则需要创建中间表
    @TableField(exist = false)
    @JsonIgnore
    @NotFound(action= NotFoundAction.IGNORE)
    private Role role;

    @ManyToOne
    @JoinColumn(name = "ownerId",insertable = false,updatable = false) //表示对应子表的关联外键，如果不使用这个注解则需要创建中间表
    @TableField(exist = false)
    @JsonIgnore
    @NotFound(action=NotFoundAction.IGNORE)
    private Group group;

    @ManyToOne
    @JoinColumn(name = "ownerId",insertable = false,updatable = false) //表示对应子表的关联外键，如果不使用这个注解则需要创建中间表
    @TableField(exist = false)
    @JsonIgnore
    @NotFound(action= NotFoundAction.IGNORE)
    private User user;
}