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
 * @Package com.liv.api.auth.dao.datamodel
 * @Description: 菜单表
 * @date 2020.5.25  17:45
 * @email 453826286@qq.com
 */

public class MenuQuery extends Menu implements BaseQuery<Menu> {
    /**
     * 组装查询条件
     * */
    @Override
    public QueryWrapper<Menu> buildQueryWrapper() {
        //这里用接口中的，具有默认排序实现
        QueryWrapper<Menu> qw = BaseQuery.super.buildQueryWrapper();

        if(StringUtils.isNotEmpty(this.getMenuName())){
            qw.like(getMpColumnName("menuName"),this.getMenuName());
        }
        if(this.getParentId()!=null){
            qw.eq(getMpColumnName("parentId"),this.getParentId());
        }
        if(this.getAccessCtrl()!=null){
            qw.eq(getMpColumnName("accessCtrl"),this.getAccessCtrl());
        }
        if(this.getIsEnabled()!=null){
            qw.eq(getMpColumnName("isEnabled"),this.getIsEnabled());
        }
        if(this.getIsHidden()!=null){
            qw.eq(getMpColumnName("isHidden"),this.getIsHidden());
        }
        if(this.getIsMenu()!=null){
            qw.eq(getMpColumnName("isMenu"),this.getIsMenu());
        }
        return qw;
    }

    /**
     * 组装查询条件
     * */
    @Override
    public List<Predicate> buildPredicates(Root<Menu> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder){
        //这里用接口中的方法，具有默认排序实现
        List<Predicate> predicates = BaseQuery.super.buildPredicates(root,criteriaQuery,criteriaBuilder);

        //实体类字段名称
        if(StringUtils.isNotEmpty(this.getMenuName())){
            predicates.add(criteriaBuilder.like(root.get("menuName"), "%"+this.getMenuName()+"%"));
        }
        if(this.getParentId()!=null){
            predicates.add(criteriaBuilder.equal(root.get("parentId"), this.getParentId()));
        }
        if(this.getAccessCtrl()!=null){
            predicates.add(criteriaBuilder.equal(root.get("accessCtrl"), this.getAccessCtrl()));
        }
        if(this.getIsEnabled()!=null){
            predicates.add(criteriaBuilder.equal(root.get("isEnabled"), this.getIsEnabled()));
        }
        if(this.getIsHidden()!=null){
            predicates.add(criteriaBuilder.equal(root.get("isHidden"), this.getIsHidden()));
        }
        if(this.getIsMenu()!=null){
            predicates.add(criteriaBuilder.equal(root.get("isMenu"), this.getIsMenu()));
        }
        return predicates;
    };

}
