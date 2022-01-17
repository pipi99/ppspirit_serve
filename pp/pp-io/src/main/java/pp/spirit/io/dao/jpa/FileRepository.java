package pp.spirit.io.dao.jpa;

import org.springframework.stereotype.Repository;
import pp.spirit.base.base.BaseRepository;
import pp.spirit.io.pojo.PPFile;

/**
 * @author LiV
 * @Title:
 * @Package com.liv.api.io.dao
 * @Description:
 * @date 2021.2.25  17:30
 * @email 453826286@qq.com
 */
@Repository
public interface FileRepository extends BaseRepository<PPFile,Long> {
}
