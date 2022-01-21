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
 * @author LiV
 * @Title:
 * @Package com.liv.api.auth.domainmodel
 * @Description: 用户查询类
 * @date 2020.6.9  10:11
 * @email 453826286@qq.com
 */
public class UserQuery extends User implements BaseQuery<User> {

    @Override
    public QueryWrapper<User> buildQueryWrapper() {
        QueryWrapper qw = BaseQuery.super.buildQueryWrapper();
        if(this.getUserId()!=null){
            qw.eq(getMpColumnName("userId"),this.getUserId());
        }
        if(StringUtils.isNotEmpty(this.getUserName())){
            qw.eq(getMpColumnName("userName"),this.getUserName());
        }
        if(StringUtils.isNotEmpty(this.getAlias())){
            qw.like(getMpColumnName("alias"),this.getAlias());
        }
        if(this.getOrgId()!=null){
            qw.eq(getMpColumnName("orgId"),this.getOrgId());
        }
        return qw;
    }

    @Override
    public List<Predicate> buildPredicates(Root<User> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder){
        //这里用接口中的方法，具有默认排序实现
        List<Predicate> predicates = BaseQuery.super.buildPredicates(root,criteriaQuery,criteriaBuilder);

        //实体类字段名称
        if(this.getUserId()!=null){
            predicates.add(criteriaBuilder.equal(root.get("userId"), this.getUserId()));
        }
        //实体类字段名称
        if(StringUtils.isNotEmpty(this.getUserName())){
            predicates.add(criteriaBuilder.equal(root.get("userName"), this.getUserName()));
        }
        //实体类字段名称
        if(StringUtils.isNotEmpty(this.getAlias())){
            predicates.add(criteriaBuilder.like(root.get("alias"), "%"+this.getAlias()+"%"));
        }
        //实体类字段名称
        if(this.getOrgId()!=null){
            predicates.add(criteriaBuilder.equal(root.get("orgId"), this.getOrgId()));
        }
        return predicates;
    }
}
