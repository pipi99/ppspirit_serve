package pp.spirit.security.pojo;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.collect.Lists;
import org.springframework.data.jpa.domain.Specification;
import pp.spirit.base.base.BaseQuery;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;

public class OwnerPermissionQuery extends OwnerPermission implements BaseQuery<OwnerPermission> {
    /**
     * 组装查询条件
     */
    @Override
    public QueryWrapper<OwnerPermission> getQueryWrapper() {
        QueryWrapper qw = new QueryWrapper();
        if(this.getOwnerId() != null){
            qw.eq(getMpColumnName("ownerId"),this.getOwnerId());
        }
        if(this.getPermissionId() != null){
            qw.eq(getMpColumnName("permissionId"),this.getPermissionId());
        }
        return qw;
    }

    /**
     * 组装查询条件
     */
    @Override
    public Specification<OwnerPermission> getSpecification() {
        OwnerPermission bean = this;
        return new Specification<OwnerPermission>(){
            @Override
            public Predicate toPredicate(Root<OwnerPermission> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                //root.get("address")表示获取address这个字段名称
                List<Predicate> predicates = Lists.newArrayList();
                //实体类字段名称
                if(bean.getOwnerId() != null){
                    predicates.add(criteriaBuilder.equal(root.get("ownerId"), bean.getOwnerId()));
                }
                if(bean.getPermissionId() != null){
                    predicates.add(criteriaBuilder.equal(root.get("permissionId"), bean.getPermissionId()));
                }
                return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        };
    }
}
