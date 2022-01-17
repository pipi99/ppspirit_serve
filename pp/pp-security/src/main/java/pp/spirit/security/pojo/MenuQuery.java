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

public class MenuQuery extends Menu implements BaseQuery<Menu> {

    /**
     * 组装查询条件
     */
    @Override
    public QueryWrapper<Menu> getQueryWrapper() {
        QueryWrapper<Menu> qw = new QueryWrapper<>();
        if(StringUtils.isNotEmpty(this.getMenuName())){
            qw.like(getMpColumnName("menuName"),this.getMenuName());
        }
        if(this.getParentId()!=null){
            qw.eq(getMpColumnName("parentId"),this.getParentId());
        }
        if(this.getAccessCtrl()!=null){
            qw.eq(getMpColumnName("accessCtrl"),this.getAccessCtrl());
        }
        if(this.getEnabled()!=null){
            qw.eq(getMpColumnName("enabled"),this.getEnabled());
        }
        if(this.getIsMenu()!=null){
            qw.eq(getMpColumnName("isMenu"),this.getIsMenu());
        }
        return qw;
    }

    /**
     * 组装查询条件
     */
    @Override
    public Specification<Menu> getSpecification() {
        Menu bean = this;
        return new Specification<Menu>(){
            @Override
            public Predicate toPredicate(Root<Menu> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                //root.get("address")表示获取address这个字段名称
                List<Predicate> predicates = Lists.newArrayList();
                //实体类字段名称
                if(StringUtils.isNotEmpty(bean.getMenuName())){
                    predicates.add(criteriaBuilder.like(root.get("menuName"), "%"+bean.getMenuName()+"%"));
                }
                if(bean.getParentId()!=null){
                    predicates.add(criteriaBuilder.equal(root.get("parentId"), bean.getParentId()));
                }
                if(bean.getAccessCtrl()!=null){
                    predicates.add(criteriaBuilder.equal(root.get("accessCtrl"), bean.getAccessCtrl()));
                }
                if(bean.getEnabled()!=null){
                    predicates.add(criteriaBuilder.equal(root.get("enabled"), bean.getEnabled()));
                }
                if(bean.getIsMenu()!=null){
                    predicates.add(criteriaBuilder.equal(root.get("isMenu"), bean.getIsMenu()));
                }
                return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        };
    }
}
