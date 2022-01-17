package com.pp.pojo;

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
 * @Title: 查询工具类
 * @Package com.liv.sp.dao.datamodel
 * @Description: 查询实体类
 * @date 2020.12.11  17:41
 * @email 453826286@qq.com
 */
public class SpQuery extends Sp implements BaseQuery<Sp> {
    /**
     * 组装mybatis查询条件
     */
    @Override
    public QueryWrapper<Sp> getQueryWrapper() {
        QueryWrapper qw = new QueryWrapper();
        if(StringUtils.isNotEmpty(this.getName())){
            //实体类字段名称
            qw.like(getMpColumnName("name"),this.getName());
        }
        return qw;
    }

    /**
     * 组装jpa查询条件
     */
    @Override
    public Specification<Sp> getSpecification() {
        Sp bean = this;
        return new Specification<Sp>(){
            @Override
            public Predicate toPredicate(Root<Sp> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                //root.get("address")表示获取address这个字段名称
                List<Predicate> predicates = Lists.newArrayList();
                //实体类字段名称
                if(StringUtils.isNotEmpty(bean.getName())){
                    predicates.add(criteriaBuilder.like(root.get("name"), "%"+bean.getName()+"%"));
                }
                return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        };
    }
}
