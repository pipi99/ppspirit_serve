package pp.spirit.security.service.impl;

import org.springframework.stereotype.Service;
import pp.spirit.base.base.BaseService;
import pp.spirit.security.dao.jpa.UserRoleRepository;
import pp.spirit.security.dao.mapper.UserRoleMapper;
import pp.spirit.security.pojo.UserRole;
import pp.spirit.security.service.UserRoleService;
@Service
public class UserRoleServiceImpl extends BaseService<Long,UserRole, UserRoleMapper, UserRoleRepository> implements UserRoleService {
}
