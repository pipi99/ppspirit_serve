package pp.spirit.security.service;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pp.spirit.base.base.BaseService;
import pp.spirit.security.dao.jpa.GroupRepository;
import pp.spirit.security.dao.mapper.GroupMapper;
import pp.spirit.security.pojo.Group;
import pp.spirit.security.pojo.GroupRole;
import pp.spirit.security.service.impl.GroupRoleServiceImpl;

import javax.transaction.Transactional;
import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GroupService extends BaseService<Long, Group, GroupMapper, GroupRepository> {
    @Autowired
    private GroupRoleServiceImpl groupRoleService;

    @Autowired
    private OwnerPermissionService ownerPermissionService;

    @Override
    @Transactional
    public boolean save(Group group) {
        this.getMapper().insert(group);
        this.saveGroupRole(group);
        return true;
    }

    @Override
    @Transactional
    public boolean updateById(Group group) {
        QueryWrapper qw = new QueryWrapper();
        qw.eq("GROUP_ID",group.getGroupId());
        groupRoleService.remove(qw);

        this.getMapper().updateById(group);
        this.saveGroupRole(group);
        return true;
    }

    @Transactional
    @Override
    public boolean removeById(Serializable id) {
        Group group = this.getRepository().findById((Long)id).get();
        //有组或者用户使用此角色
        if(group.getUsers()!=null && group.getUsers().size()>0){
            throw new RuntimeException("此组被用户使用中，无法删除，请确认用户未使用此组");
        }
        //删除组权限
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("OWNER_ID",id);
        ownerPermissionService.remove(queryWrapper);

        //删除组角色
        QueryWrapper qw = new QueryWrapper();
        qw.eq("GROUP_ID",id);
        groupRoleService.remove(qw);

        //删除组
        return SqlHelper.retBool(this.getBaseMapper().deleteById(id));
    }


    private void saveGroupRole(Group group){
//        新增角色关联关系
        List<Long> roleIds= group.getRoleIds();
        if(roleIds!=null){
            List<GroupRole> GroupRoles = roleIds.stream().map(roleId->{
                return new GroupRole(null,roleId,group.getGroupId());
            }).collect(Collectors.toList());
            groupRoleService.getRepository().saveAll(GroupRoles);
            groupRoleService.getRepository().flush();
        }
    }
}

