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

public class RoleQuery extends Role implements BaseQuery<Role> {

    /**
     * 组装查询条件
     */
    @Override
    public QueryWrapper<Role> getQueryWrapper() {
        QueryWrapper<Role> qw = new QueryWrapper<>();
        if(StringUtils.isNotEmpty(this.getRoleName())){
            qw.like(getMpColumnName("roleName"),this.getRoleName());
        }
        if(StringUtils.isNotEmpty(this.getRoleAlias())){
            qw.like(getMpColumnName("roleAlias"),this.getRoleAlias());
        }
        return qw;
    }

    /**
     * 组装JPA查询条件
     */
    @Override
    public Specification<Role> getSpecification() {
        Role bean = this;
        return new Specification<Role>(){
            @Override
            public Predicate toPredicate(Root<Role> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                //root.get("address")表示获取address这个字段名称
                List<Predicate> predicates = Lists.newArrayList();
                //实体类字段名称
                if(StringUtils.isNotEmpty(bean.getRoleName())){
                    predicates.add(criteriaBuilder.like(root.get("roleName"), "%"+bean.getRoleName()+"%"));
                }
                //实体类字段名称
                if(StringUtils.isNotEmpty(bean.getRoleAlias())){
                    predicates.add(criteriaBuilder.like(root.get("roleAlias"), "%"+bean.getRoleAlias()+"%"));
                }
                return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        };
    }
}