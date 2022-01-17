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
 * @Package com.liv.api.base.dict
 * @Description: 数据字典实体
 * @date 2020.6.9  14:26
 * @email 453826286@qq.com
 */
public class DictTypeQuery extends DictType implements BaseQuery<DictType> {
    /**
     * 组装查询条件
     */
    @Override
    public QueryWrapper<DictType> getQueryWrapper() {
        QueryWrapper qw = new QueryWrapper();
        if(StringUtils.isNotEmpty(this.getDictTypeName())){
            qw.like(getMpColumnName("dictTypeName"),this.getDictTypeName());
        }
        if(StringUtils.isNotEmpty(this.getDictTypeCode())){
            qw.like(getMpColumnName("dictTypeCode"),this.getDictTypeCode());
        }
        return qw;
    }

    /**
     * 组装查询条件
     */
    @Override
    public Specification<DictType> getSpecification() {
        DictType bean = this;
        return new Specification<DictType>(){
            @Override
            public Predicate toPredicate(Root<DictType> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                //root.get("address")表示获取address这个字段名称
                List<Predicate> predicates = Lists.newArrayList();
                //实体类字段名称
                if(StringUtils.isNotEmpty(bean.getDictTypeName())){
                    predicates.add(criteriaBuilder.like(root.get("dictTypeName"), "%"+bean.getDictTypeName()+"%"));
                }

                if(StringUtils.isNotEmpty(bean.getDictTypeCode())){
                    predicates.add(criteriaBuilder.like(root.get("dictTypeCode"), "%"+bean.getDictTypeCode()+"%"));
                }

                return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        };
    }
}
