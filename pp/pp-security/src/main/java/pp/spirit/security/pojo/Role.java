package pp.spirit.security.pojo;


import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
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
import pp.spirit.base.utils.ContextUtils;
import pp.spirit.base.validate.ValidException;
import pp.spirit.security.service.RoleService;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.io.Serializable;
import java.util.List;

@Data
@Entity
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="Role", description="角色实体")
public class Role extends BaseBean<Role> implements Serializable {
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
    private Long roleId;

    /**
     * 名称
     *
     * Nullable:  true
     */
    @NotBlank(message = "角色名称不能为空")
    @Length(min = 2,max = 32)
    @Pattern(regexp = "^\\w+[a-zA-Z0-9_]+",message = "请输入正确的角色代码，字母开头英文数字下划线组合")
    private String roleName;

    /**
     * 名称
     *
     * Nullable:  true
     */
    @NotBlank(message = "角色说明不能为空")
    @Length(min = 2,max = 32)
    private String roleAlias;

    /**
     * 角色权限
     **/
    @TableField(exist = false)
    @ManyToMany(cascade = {CascadeType.ALL},fetch = FetchType.EAGER)
    @JoinTable(name="OwnerPermission",joinColumns={@JoinColumn(name="ownerId",referencedColumnName="roleId")},inverseJoinColumns={@JoinColumn(name="permissionId",referencedColumnName="permissionId")})
    private List<Permission> permissions ;

    //所属组
    @ManyToMany(mappedBy = "roles",fetch=FetchType.EAGER)
    @TableField(exist = false)
    @Fetch(FetchMode.SUBSELECT)
    @JsonIgnore
    private List<Group> groups;

    @ManyToMany(mappedBy = "roles",fetch=FetchType.EAGER)
    @TableField(exist = false)
    @Fetch(FetchMode.SUBSELECT)
    @JsonIgnore
    private List<User> users;
    /**
     * 角色说明
     *
     * Nullable:  true
     */
    @Length(max = 100)
    private String description;

    @Override
    public void valid(Role role){
        RoleService service = ContextUtils.getBean("roleService", RoleService.class);

        QueryWrapper<Role> queryWrapper = new QueryWrapper<>();
        if(role.getRoleId()!=null){
            queryWrapper.ne("ROLE_ID",role.getRoleId());
        }
        queryWrapper.eq("ROLE_NAME",role.getRoleName());
        List list = service.list(queryWrapper);

        //执行校验
        if(list!=null&&list.size()>0){
            throw new ValidException("角色名称不能重复！");
        }
//        if(SecurityConst.ROLE_ADMINISTRATOR.equals(role.getRoleName())|| SecurityConst.ROLE_GUEST.equals(role.getRoleName())||
//                SecurityConst.ROLE_ANONYMOUS.equals(role.getRoleName())||
//                SecurityConst.ROLE_SUPER_ADMINISTRATOR.equals(role.getRoleName())){
//            throw new ValidException("角色名称不能为系统保留角色名（ROLE_SUPER_ADMINISTRATOR，ROLE_ADMINISTRATOR，ROLE_ANONYMOUS，ROLE_GUEST）！");
//        }
    }

    private static final long serialVersionUID = 1L;
}