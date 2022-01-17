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

public class MenuActionsQuery extends MenuActions implements BaseQuery<MenuActions> {

    /**
     * 组装查询条件
     */
    @Override
    public QueryWrapper<MenuActions> getQueryWrapper() {
        QueryWrapper<MenuActions> qw = new QueryWrapper<>();
        if(this.getMenuId()!=null){
            qw.eq(getMpColumnName("menuId"),this.getMenuId());
        }
        if(StringUtils.isNotEmpty(this.getActionName())){
            qw.like(getMpColumnName("actionName"),this.getActionName());
        }
        return qw;
    }

    /**
     * 组装查询条件
     */
    @Override
    public Specification<MenuActions> getSpecification() {
        MenuActions bean = this;
        return new Specification<MenuActions>(){
            @Override
            public Predicate toPredicate(Root<MenuActions> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                //root.get("address")表示获取address这个字段名称
                List<Predicate> predicates = Lists.newArrayList();
                if(bean.getMenuId()!=null){
                    predicates.add(criteriaBuilder.equal(root.get("menuId"), bean.getMenuId()));
                }
                if(StringUtils.isNotEmpty(bean.getActionName())){
                    predicates.add(criteriaBuilder.like(root.get("actionName"), "%"+bean.getActionName()+"%"));
                }
                return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        };
    }
}
