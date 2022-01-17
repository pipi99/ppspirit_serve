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
 * <p>
 * 组织机构表
 * </p>
 *
 * @author Liv
 * @since 2020-05-21
 */
public class OrganQuery extends Organ implements BaseQuery<Organ> {
    /**
     * 组装查询条件
     */
    @Override
    public QueryWrapper<Organ> getQueryWrapper() {
        QueryWrapper<Organ> qw = new QueryWrapper<>();
        if(StringUtils.isNotEmpty(this.getOrganName())){
            qw.like("ORGAN_NAME",this.getOrganName());
        }
        if(StringUtils.isNotEmpty(this.getOrganLevel())){
            qw.eq("ORGAN_LEVEL",this.getOrganLevel());
        }
        if(StringUtils.isNotEmpty(this.getOrganType())){
            qw.eq("ORGAN_TYPE",this.getOrganType());
        }
        return qw;
    }

    /**
     * 组装查询条件
     */
    @Override
    public Specification<Organ> getSpecification() {
        Organ bean = this;
        return new Specification<Organ>(){
            @Override
            public Predicate toPredicate(Root<Organ> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                //root.get("address")表示获取address这个字段名称
                List<Predicate> predicates = Lists.newArrayList();
                //实体类字段名称
                if(StringUtils.isNotEmpty(bean.getOrganName())){
                    predicates.add(criteriaBuilder.like(root.get("name"), "%"+bean.getOrganName()+"%"));
                }

                return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        };
    }
}
