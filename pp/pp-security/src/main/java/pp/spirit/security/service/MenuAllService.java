package pp.spirit.security.service;

import org.springframework.stereotype.Service;
import pp.spirit.base.base.BaseService;
import pp.spirit.security.dao.jpa.MenuAllRepository;
import pp.spirit.security.dao.mapper.MenuAllMapper;
import pp.spirit.security.pojo.MenuAll;

@Service
public class MenuAllService extends BaseService<Long, MenuAll, MenuAllMapper, MenuAllRepository> {
}