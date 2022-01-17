package pp.spirit.security.service.impl;


import org.springframework.stereotype.Service;
import pp.spirit.base.base.BaseService;
import pp.spirit.security.dao.jpa.GroupRoleRepository;
import pp.spirit.security.dao.mapper.GroupRoleMapper;
import pp.spirit.security.pojo.GroupRole;
import pp.spirit.security.service.GroupRoleService;

@Service
public class GroupRoleServiceImpl extends BaseService<Long,GroupRole, GroupRoleMapper, GroupRoleRepository> implements GroupRoleService {

}
