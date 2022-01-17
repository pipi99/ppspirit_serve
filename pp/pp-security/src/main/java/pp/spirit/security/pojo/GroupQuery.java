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

public class GroupQuery extends Group implements BaseQuery<Group> {

    /**
     * 组装查询条件
     */
    @Override
    public QueryWrapper<Group> getQueryWrapper() {
        QueryWrapper<Group> qw = new QueryWrapper<>();
        if(StringUtils.isNotEmpty(this.getGroupName())){
            qw.like(getMpColumnName("groupName"),this.getGroupName());
        }
        return qw;
    }

    /**
     * 组装查询条件
     */
    @Override
    public Specification<Group> getSpecification() {
        Group bean = this;
        return new Specification<Group>(){
            @Override
            public Predicate toPredicate(Root<Group> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                //root.get("address")表示获取address这个字段名称
                List<Predicate> predicates = Lists.newArrayList();
                //实体类字段名称
                if(StringUtils.isNotEmpty(bean.getGroupName())){
                    predicates.add(criteriaBuilder.like(root.get("groupName"), "%"+bean.getGroupName()+"%"));
                }

                return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        };
    }
}