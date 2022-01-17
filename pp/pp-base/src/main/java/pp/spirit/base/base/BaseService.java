package pp.spirit.base.base;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.Serializable;

/**
 * @author LiV
 * @Title:
 * @Package com.liv.web.base
 * @Description: Service操作基类
 * @date 2020.4.13  17:18
 * @email 453826286@qq.com
 */
@SuppressWarnings("ALL")
public abstract  class BaseService<ID extends Serializable,T,M extends BaseMapper<T>, R extends BaseRepository<T,ID>> extends ServiceImpl<M,T> implements IBaseService<ID,T,M,R>{

    /**jpa*/
    @Autowired
    private R repository;

    /**
     * mybatis-plus mapper
     */
    public M getMapper() {
        return this.getBaseMapper();
    }

    /**
     * jpa
     */
    @Override
    public R getRepository() {
        return this.repository;
    }

}
