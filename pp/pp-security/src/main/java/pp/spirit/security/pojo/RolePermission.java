package pp.spirit.security.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Table: user
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "RolePermission", description = "角色权限实体")
@TableName("role_permission")
public class RolePermission implements Serializable {
    /**
     * 主键
     *
     * Nullable:  false
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long permissionId;

    /**
     * 权限类型： CRUD
     *
     * Nullable:  true
     */
    private String permission;
    /**
     * 资源ID
     *
     * Nullable:  true
     */
    private Long  resourceId;

    private static final long serialVersionUID = 1L;
}