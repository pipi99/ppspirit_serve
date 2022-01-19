package pp.spirit.security.pojo;


import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fasterxml.jackson.annotation.JsonFormat;
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
import pp.spirit.security.service.UserService;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Table: user
 */
@Data
@Entity
@Table(name = "pp_user")
@TableName("pp_user")
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="User", description="用户实体类")
public class User extends BaseBean<User> implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @TableId
    @GeneratedValue(generator = IdGenerator.GEN_NAME,strategy = GenerationType.SEQUENCE)
    @GenericGenerator(name = IdGenerator.GEN_NAME, strategy = IdGenerator.GEN_CLASS_NAME)
    @ApiModelProperty(value = "主键")
    private Long userId;

    /**
     * 用户名称
     *
     * Nullable:  true
     */
    @NotBlank(message = "用户名称不能为空")
    @Length(max=50)
    private String userName;

    /**
     * 密码
     *
     * Nullable:  true
     */
    private String password;

    /**
     * 组织机构
     *
     * Nullable:  true
     */
    @NotNull(message = "请选择所属机构")
    private Long orgId;

    /**
     * 姓名
     *
     * Nullable:  true
     */
    @NotBlank(message = "姓名不能为空")
    @Length(max=50)
    private String alias;

    /**
     * 手机
     *
     * Nullable:  true
     */
    @Length(max=50)
    private String mobile;

    /**
     * 邮箱
     *
     * Nullable:  true
     */
//    @Pattern(regexp ="^([A-Za-z0-9_\\-\\.])+\\@([A-Za-z0-9_\\-\\.])+\\.([A-Za-z]{2,4})$")
    @Length(max=50)
    private String email;

    /**
     * 性别
     *
     * Nullable:  true
     */
    private Integer gender;

    /**
     * 学历
     *
     * Nullable:  true
     */
    @Length(max=50)
    private String degree;

    /**
     * 出生年月日
     *
     * Nullable:  true
     */
    @JsonFormat(pattern="yyyy-MM-dd'T'HH:mm:ss")
    private Date birthday;

    /**
     * 照片
     *
     * Nullable:  true
     */
    @Length(max=100)
    private String photo;

    /**
     * 创建日期
     * Nullable:  true
     */
    private Date createDate;

    /**
     * @Author: LiV
     * @Date: 2020.4.19 20:42
     * @Description: 是否锁定  1是 0否
     **/
    private Integer locked;
    /**
     * 锁定时间
     *
     * Nullable:  true
     */
    private Date lockTime;

    /**
     * 是否启用   1是 0否
     **/
    private Integer enabled;

    /**
     * 账号是否过期   1是 0否
     **/
    private Integer expired;

    /**
     * 凭证（密码）是否过期   1是 0否
     **/
    private Integer credentialsExpired;

    /**
     * 原始资源ID
     **/
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "orgId",insertable = false,updatable = false) //表示对应子表的关联外键，如果不使用这个注解则需要创建中间表
    @TableField(exist = false)
    @JsonIgnore
    private Organ organ;

    /**
     * 用户权限
     **/
    @TableField(exist = false)
    @ManyToMany(cascade = {CascadeType.ALL},fetch = FetchType.EAGER)
    @JoinTable(name="OwnerPermission",
                joinColumns={@JoinColumn(name="ownerId",referencedColumnName="userId")},
                inverseJoinColumns={@JoinColumn(name="permissionId",referencedColumnName="permissionId")})
    private List<Permission> permissions ;

    /**
     * 用户组  保存的时候使用
     **/
    @TableField(exist = false)
    @Transient
    private List<Long> groupIds;

    public List<Long> getGroupIds(){
        if(groupIds==null&&groups!=null)
            return groups.stream().map(Group::getGroupId).collect(Collectors.toList());
        else
            return groupIds;
    }

    @TableField(exist = false)
    @ManyToMany(cascade = {CascadeType.ALL},fetch = FetchType.EAGER)
    @Fetch(FetchMode.SUBSELECT)
    @JoinTable(name="userGroup",
            joinColumns={@JoinColumn(name="userId")}
            ,inverseJoinColumns={@JoinColumn(name="groupId")})
    private List<Group> groups;

    /**
     * 用户角色  保存的时候使用
     **/
    @TableField(exist = false)
    @Transient
    private List<Long> roleIds;

    public List<Long> getRoleIds(){
        if(roleIds==null&&roles!=null)
            return roles.stream().map(Role::getRoleId).collect(Collectors.toList());
        else
            return roleIds;
    }

    @TableField(exist = false)
    @ManyToMany(cascade = {CascadeType.ALL},fetch = FetchType.EAGER)
    @Fetch(FetchMode.SUBSELECT)
    @JoinTable(name="userRole",
            joinColumns={@JoinColumn(name="userId")}
            ,inverseJoinColumns={@JoinColumn(name="roleId")})
    private List<Role> roles;

    @Override
    public void valid(User user){
        UserService service = ContextUtils.getBean("userService", UserService.class);
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        if (user.getUserId() != null) {
            queryWrapper.ne("USER_ID", user.getUserId());
        }
        queryWrapper.eq("USER_NAME", user.getUserName());
        List list = service.list(queryWrapper);

        //执行校验
        if (list != null && list.size() > 0) {
            throw new ValidException("用户登录名称不能重复！");
        }
    }
}