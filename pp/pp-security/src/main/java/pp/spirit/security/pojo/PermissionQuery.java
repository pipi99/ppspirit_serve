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

public class PermissionQuery extends Permission implements BaseQuery<Permission> {
    /**
     * 组装查询条件
     */
    @Override
    public QueryWrapper<Permission> getQueryWrapper() {
        QueryWrapper qw = new QueryWrapper();
        if(StringUtils.isNotEmpty(this.getPermission())){
            qw.like(getMpColumnName("permission"),this.getPermission());
        }
        return qw;
    }

    /**
     * 组装查询条件
     */
    @Override
    public Specification<Permission> getSpecification() {
        Permission bean = this;
        return new Specification<Permission>(){
            @Override
            public Predicate toPredicate(Root<Permission> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                //root.get("address")表示获取address这个字段名称
                List<Predicate> predicates = Lists.newArrayList();
                //实体类字段名称
                if(StringUtils.isNotEmpty(bean.getPermission())){
                    predicates.add(criteriaBuilder.like(root.get("permission"), "%"+bean.getPermission()+"%"));
                }
                return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        };
    }
}
