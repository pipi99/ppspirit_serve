package pp.spirit.security.service;

import org.springframework.stereotype.Service;
import pp.spirit.base.base.BaseService;
import pp.spirit.security.dao.jpa.DictTypeRepository;
import pp.spirit.security.dao.mapper.DictTypeMapper;
import pp.spirit.security.pojo.DictType;

@Service
public class DictTypeService extends BaseService<Long, DictType, DictTypeMapper, DictTypeRepository> {
}
