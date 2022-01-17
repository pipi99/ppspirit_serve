package pp.spirit.security.service;


import org.springframework.stereotype.Service;
import pp.spirit.base.base.BaseService;
import pp.spirit.security.dao.jpa.DictRepository;
import pp.spirit.security.dao.mapper.DictMapper;
import pp.spirit.security.pojo.Dict;

@Service
public class DictService extends BaseService<Long, Dict, DictMapper, DictRepository> {
}
