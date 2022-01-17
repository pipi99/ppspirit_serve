package pp.spirit.io.service;

import org.springframework.stereotype.Service;
import pp.spirit.base.base.BaseService;
import pp.spirit.io.dao.jpa.FileRepository;
import pp.spirit.io.dao.mapper.FileMapper;
import pp.spirit.io.pojo.PPFile;

/**
 * @author LiV
 * @Title:
 * @Package com.liv.sp.service
 * @Description: 服务层
 * @date 2020.12.11  11:23
 * @email 453826286@qq.com
 */
@Service
public class FileService extends BaseService<Long, PPFile, FileMapper, FileRepository> {

}
