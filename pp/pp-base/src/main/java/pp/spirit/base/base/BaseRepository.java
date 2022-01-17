package pp.spirit.base.base;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * @author LW
 * @title: BaseRepository
 * @projectName spirit
 * @description: jpa 基类
 * @date 2021.11.1916:40
 */
@NoRepositoryBean
public interface BaseRepository<T, ID extends Serializable> extends JpaRepository<T,ID>, JpaSpecificationExecutor<T> {

    /**
     * 转换jpa的page为mybatis-plus 的page
     * @param query
     * @return
     */
    default com.baomidou.mybatisplus.extension.plugins.pagination.Page findAllReturnMybatisPlus(BaseQuery query){
        org.springframework.data.domain.Page _page = this.findAll(query.getSpecification(),query.getJpaPage());
        com.baomidou.mybatisplus.extension.plugins.pagination.Page page = new com.baomidou.mybatisplus.extension.plugins.pagination.Page();
        //结果集转换为ArrayList，避免迭代器报错
        ArrayList list = new ArrayList();
        list.addAll(_page.getContent());
        page.setRecords(list);
        page.setTotal(_page.getTotalElements());
        page.setSize(_page.getSize());
        page.setCurrent(_page.getPageable().getPageNumber()+1);
        page.setPages(_page.getTotalPages());

        //
        page.setOrders(((BaseBean)query).getOrders());
        return page;
    }
}
