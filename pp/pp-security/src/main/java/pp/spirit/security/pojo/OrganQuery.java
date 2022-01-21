package pp.spirit.security.pojo;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.apache.commons.lang3.StringUtils;
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
     * */
    @Override
    public QueryWrapper<Organ> buildQueryWrapper() {
        //这里用接口中的，具有默认排序实现
        QueryWrapper<Organ> qw = BaseQuery.super.buildQueryWrapper();
        if(StringUtils.isNotEmpty(this.getOrganName())){
            qw.like("ORGAN_NAME",this.getOrganName());
        }
        if(StringUtils.isNotEmpty(this.getOrganLevel())){
            qw.eq("ORGAN_LEVEL",this.getOrganLevel());
        }
        if(StringUtils.isNotEmpty(this.getOrganType())){
            qw.eq("ORGAN_TYPE",this.getOrganType());
        }
        if(this.getEnabled()!=null){
            qw.eq("enabled",this.getEnabled());
        }
        return qw;
    }

    /**
     * 组装查询条件
     * */
    @Override
    public List<Predicate> buildPredicates(Root<Organ> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder){
        //这里用接口中的方法，具有默认排序实现
        List<Predicate> predicates = BaseQuery.super.buildPredicates(root,criteriaQuery,criteriaBuilder);

        //实体类字段名称
        if(StringUtils.isNotEmpty(this.getOrganName())){
            predicates.add(criteriaBuilder.like(root.get("name"), "%"+this.getOrganName()+"%"));
        }

        //这里用接口中的，具有默认排序实现
        QueryWrapper<Organ> qw = BaseQuery.super.buildQueryWrapper();
        if(StringUtils.isNotEmpty(this.getOrganName())){
            predicates.add(criteriaBuilder.like(root.get("organName"), "%"+this.getOrganName()+"%"));
        }
        if(StringUtils.isNotEmpty(this.getOrganLevel())){
            predicates.add(criteriaBuilder.equal(root.get("organLevel"), this.getOrganLevel()));
        }
        if(StringUtils.isNotEmpty(this.getOrganType())){
            predicates.add(criteriaBuilder.equal(root.get("organType"), this.getOrganType()));
        }
        if(this.getEnabled()!=null){
            predicates.add(criteriaBuilder.equal(root.get("enabled"), this.getEnabled()));
        }

        return predicates;
    };

}
