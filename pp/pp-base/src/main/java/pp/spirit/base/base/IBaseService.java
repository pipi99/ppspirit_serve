package pp.spirit.base.base;

import com.baomidou.mybatisplus.extension.service.IService;

import java.io.Serializable;

/**
 * @author LiV
 * @Title:
 * @Package com.liv.api.base.base
 * @Description:
 * @date 2021.2.23  14:42
 * @email 453826286@qq.com
 */
public interface  IBaseService<ID extends Serializable,T,M extends BaseMapper<T>,R extends BaseRepository<T,ID>> extends IService<T> {
    /**mybatis-plus mapper*/
    M getMapper();
    /**jpa*/
    R getRepository();
}
