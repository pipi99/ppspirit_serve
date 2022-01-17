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
 * @Package com.liv.api.auth.domainmodel
 * @Description: 用户查询类
 * @date 2020.6.9  10:11
 * @email 453826286@qq.com
 */
public class UserQuery extends User implements BaseQuery<User> {

    @Override
    public QueryWrapper<User> getQueryWrapper() {
        QueryWrapper qw = new QueryWrapper();
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

    /**
     * 组装查询条件
     */
    @Override
    public Specification<User> getSpecification() {
        User bean = this;
        return new Specification<User>(){
            @Override
            public Predicate toPredicate(Root<User> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                //root.get("address")表示获取address这个字段名称
                List<Predicate> predicates = Lists.newArrayList();
                if(bean.getUserId()!=null){
                    predicates.add(criteriaBuilder.equal(root.get("userId"), bean.getUserId()));
                }
                //实体类字段名称
                if(StringUtils.isNotEmpty(bean.getUserName())){
                    predicates.add(criteriaBuilder.equal(root.get("userName"), bean.getUserName()));
                }
                //实体类字段名称
                if(StringUtils.isNotEmpty(bean.getAlias())){
                    predicates.add(criteriaBuilder.like(root.get("alias"), "%"+bean.getAlias()+"%"));
                }
                //实体类字段名称
                if(bean.getOrgId()!=null){
                    predicates.add(criteriaBuilder.equal(root.get("orgId"), bean.getOrgId()));
                }

                return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        };
    }
}
