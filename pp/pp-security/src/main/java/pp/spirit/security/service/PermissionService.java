package pp.spirit.security.service;


import org.springframework.stereotype.Service;
import pp.spirit.base.base.BaseService;
import pp.spirit.security.dao.jpa.PermissionRepository;
import pp.spirit.security.dao.mapper.PermissionMapper;
import pp.spirit.security.pojo.Permission;

@Service
public class PermissionService  extends BaseService<Long, Permission, PermissionMapper, PermissionRepository> {

}

