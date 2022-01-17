package pp.spirit.security.pojo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.validator.constraints.Length;
import pp.spirit.base.base.BaseBean;
import pp.spirit.base.incrementer.IdGenerator;
import pp.spirit.base.utils.ContextUtils;
import pp.spirit.base.validate.ValidException;
import pp.spirit.security.service.PermissionService;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import java.io.Serializable;
import java.util.List;

@Data
@Entity
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value="Dict", description="权限实体")
public class Permission extends BaseBean<Permission> implements Serializable {

    public Permission(String permission){
        this.permission = permission;
    }

    @Id
    @TableId
    @GeneratedValue(generator = IdGenerator.GEN_NAME, strategy = GenerationType.SEQUENCE)
    @GenericGenerator(name = IdGenerator.GEN_NAME, strategy = IdGenerator.GEN_CLASS_NAME)
    @ApiModelProperty(value = "主键")
    private Long permissionId;

    @ApiModelProperty(value = "资源类型")
    private String resourceType;

    /**
     * 所属资源ID
     **/
    @ApiModelProperty(value = "所属资源ID")
    private Long  resourceId;

    /**
     * 原始资源ID
     **/
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "resourceId",insertable = false,updatable = false) //表示对应子表的关联外键，如果不使用这个注解则需要创建中间表
    @TableField(exist = false)
    @JsonIgnore
    private Menu menu;

    @ApiModelProperty(value = "权限名称")
    @NotEmpty
    @Length(max=100)
    @Pattern(regexp="^\\w+[a-zA-Z0-9_]*",flags = Pattern.Flag.CASE_INSENSITIVE,message = "字母开头，字母数字下划线组合")
    private String permission;

    @ApiModelProperty(value = "权限说明")
    @NotEmpty
    @Length(max=256)
    private String remark;

    //分配权限的时候，使用树形结构
    @TableField(exist = false)
    @Transient
    private List<Permission> children;

    @Override
    public void valid(Permission permission){
        PermissionService service = ContextUtils.getBean("permissionService", PermissionService.class);

        QueryWrapper<Permission> queryWrapper = new QueryWrapper<>();
        if(permission.getPermissionId()!=null){
            queryWrapper.ne("PERMISSION_ID",permission.getPermissionId());
        }
        queryWrapper.and(permissionQueryWrapper -> {
            permissionQueryWrapper.eq("PERMISSION",permission.getPermission());
            permissionQueryWrapper.or();
            permissionQueryWrapper.eq("REMARK",permission.getRemark());
        });
        List list = service.list(queryWrapper);

        //执行校验
        if(list!=null&&list.size()>0){
            throw new ValidException("权限标识或者名称不能重复！");
        }
    }
}
