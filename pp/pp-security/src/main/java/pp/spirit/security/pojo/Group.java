package pp.spirit.security.pojo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import jdk.nashorn.internal.objects.annotations.Getter;
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
import pp.spirit.security.service.GroupService;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Entity
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="Group", description="用户组实体")
@TableName("PP_GROUP")
@Table(name="PP_GROUP")//设置数据库中表名字
public class Group extends BaseBean<Group> implements Serializable {

    @Id
    @TableId
    @GeneratedValue(generator = IdGenerator.GEN_NAME,strategy = GenerationType.SEQUENCE)
    @GenericGenerator(name = IdGenerator.GEN_NAME, strategy = IdGenerator.GEN_CLASS_NAME)
    @ApiModelProperty(value = "主键")
    private Long groupId;

    /**
     * 名称
     *
     * Nullable:  true
     */
    @NotBlank(message = "名称不能为空")
    @Length(max = 32)
    private String groupName;

    /**
     * 用户组说明
     * Nullable:  true
     */
    @Length(max = 100)
    private String description;
    /**
     * 组权限
     **/
    @TableField(exist = false)
    @ManyToMany(cascade = {CascadeType.ALL},fetch = FetchType.EAGER)
    @JoinTable(name="OwnerPermission",joinColumns={@JoinColumn(name="ownerId",referencedColumnName="groupId")},inverseJoinColumns={@JoinColumn(name="permissionId",referencedColumnName="permissionId")})
    private List<Permission> permissions ;

    @ManyToMany(mappedBy = "groups",fetch=FetchType.EAGER)
    @TableField(exist = false)
    @Fetch(FetchMode.SUBSELECT)
    @JsonIgnore
    private List<User> users;

    /**
     * 组角色  保存的时候使用
     **/
    @TableField(exist = false)
    @Transient
    private List<Long> roleIds;
    @Getter
    public List<Long> getRoleIds(){
        if(roleIds==null&&roles!=null)
            return roles.stream().map(Role::getRoleId).collect(Collectors.toList());
        else
            return roleIds;
    }
    /**
     * 组角色
     **/
    @TableField(exist = false)
    @ManyToMany(cascade = {CascadeType.ALL},fetch = FetchType.EAGER)
    @JoinTable(name="GroupRole",joinColumns={@JoinColumn(name="groupId",referencedColumnName="groupId")},inverseJoinColumns={@JoinColumn(name="roleId",referencedColumnName="roleId")})
    @Fetch(FetchMode.SUBSELECT)
    private List<Role> roles ;

    @Override
    public void valid(Group group){
        GroupService service = ContextUtils.getBean("groupService", GroupService.class);

        QueryWrapper<Group> queryWrapper = new QueryWrapper<>();
        if(group.getGroupId()!=null){
            queryWrapper.ne("GROUP_ID",group.getGroupId());
        }
        queryWrapper.eq("GROUP_NAME",group.getGroupName());
        List list = service.list(queryWrapper);

        //执行校验
        if(list!=null&&list.size()>0){
            throw new ValidException("组名称不能重复！");
        }
    }
    private static final long serialVersionUID = 1L;
}