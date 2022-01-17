package pp.spirit.security.pojo;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;
import pp.spirit.base.base.BaseQuery;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;

/**
 * @author LiV
 * @Title:
 * @Package com.liv.api.auth.dao.datamodel
 * @Description: 菜单表
 * @date 2020.5.25  17:45
 * @email 453826286@qq.com
 */

public class MenuAllQuery extends MenuAll implements BaseQuery<MenuAll> {

    /**
     * 组装查询条件
     */
    @Override
    public QueryWrapper<MenuAll> getQueryWrapper() {
        QueryWrapper<MenuAll> qw = new QueryWrapper<>();
        if(StringUtils.isNotEmpty(this.getMenuName())){
            qw.like(getMpColumnName("menuName"),this.getMenuName());
        }

        return qw;
    }

    /**
     * 组装查询条件
     */
    @Override
    public Specification<MenuAll> getSpecification() {
        MenuAll bean = this;
        return new Specification<MenuAll>(){
            @Override
            public Predicate toPredicate(Root<MenuAll> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                //root.get("address")表示获取address这个字段名称
                List<Predicate> predicates = Lists.newArrayList();
                //实体类字段名称
                if(StringUtils.isNotEmpty(bean.getMenuName())){
                    predicates.add(criteriaBuilder.like(root.get("menuName"), "%"+bean.getMenuName()+"%"));
                }


                return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        };
    }
}
