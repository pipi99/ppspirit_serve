package pp.spirit.security.service;

import org.springframework.stereotype.Service;
import pp.spirit.base.base.BaseService;
import pp.spirit.security.dao.jpa.MenuActionsRepository;
import pp.spirit.security.dao.mapper.MenuActionsMapper;
import pp.spirit.security.pojo.MenuActions;

@Service
public class MenuActionsService extends BaseService<Long, MenuActions, MenuActionsMapper, MenuActionsRepository> {
}