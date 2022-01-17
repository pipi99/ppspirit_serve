package pp.spirit.security.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pp.spirit.base.base.BaseService;
import pp.spirit.security.dao.jpa.RoleRepository;
import pp.spirit.security.dao.mapper.RoleMapper;
import pp.spirit.security.pojo.Role;

import javax.transaction.Transactional;
import java.io.Serializable;

/**
 * @author LiV
 * @Title:
 * @Package com.liv.service.impl
 * @Description: 用户service
 * @date 2020.4.14  11:10
 * @email 453826286@qq.com
 */
@Service
public class RoleService  extends BaseService<Long, Role, RoleMapper, RoleRepository>  {

    @Autowired
    private OwnerPermissionService ownerPermissionService;

    @Transactional
    @Override
    public boolean removeById(Serializable id) {
        Role role = this.getRepository().findById((Long)id).get();
        //有组或者用户使用此角色
        if((role.getGroups()!=null && role.getGroups().size()>0)||(role.getUsers()!=null && role.getUsers().size()>0)){
            throw new RuntimeException("此角色使用中，无法删除，请确认用户、组未使用此角色");
        }
        //删除角色权限
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("OWNER_ID",role.getRoleId());
        ownerPermissionService.remove(queryWrapper);

        //删除角色
        return SqlHelper.retBool(this.getBaseMapper().deleteById(id));
    }

    public boolean deleteById(Long id) {
        //删除组角色关系
        QueryWrapper qw = new QueryWrapper();
        qw.eq("OWNER_ID", id);
        ownerPermissionService.remove(qw);

        //删除组
        this.removeById(id);
        return true;
    }
}
