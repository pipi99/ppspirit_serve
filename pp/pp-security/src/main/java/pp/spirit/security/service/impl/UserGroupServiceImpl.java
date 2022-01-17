package pp.spirit.security.service.impl;

import org.springframework.stereotype.Service;
import pp.spirit.base.base.BaseService;
import pp.spirit.security.dao.jpa.UserGroupRepository;
import pp.spirit.security.dao.mapper.UserGroupMapper;
import pp.spirit.security.pojo.UserGroup;
import pp.spirit.security.service.UserGroupService;

/**
 * @author LiV
 * @Title:
 * @Package com.liv.service.impl
 * @Description: 用户 service
 * @date 2020.4.14  11:11
 * @email 453826286@qq.com
 */
@Service("UserGroupRoleService")
public class UserGroupServiceImpl extends BaseService<Long, UserGroup, UserGroupMapper, UserGroupRepository> implements UserGroupService {
}
