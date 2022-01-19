package pp.spirit.base.base;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.Lists;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author LiV
 * @Title:
 * @Package com.liv.api.base.base
 * @Description: 基础查询类
 * @date 2020.7.21  10:54
 * @email 453826286@qq.com
 */
public interface BaseQuery<T> {

    /**组装查询条件
     * 供子类覆盖
     * */
    default QueryWrapper<T> buildQueryWrapper() {
        QueryWrapper<T> queryWrapper = new QueryWrapper<>();
        buildOrders(queryWrapper);
        return queryWrapper;
    }

    /**组装查询条件
     * 供子类覆盖
     * */
    default List<Predicate> buildPredicates(Root<T> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder){
        /**
         * order By
         */
        buildOrders(root,criteriaQuery,criteriaBuilder);
        return new ArrayList<>();
    };

    /**组装查询条件*/
    @JsonIgnore
    default QueryWrapper<T> getQueryWrapper(){
        QueryWrapper<T> queryWrapper = this.buildQueryWrapper();
        return queryWrapper;
    }

    /**
     * 组装查询条件
     */
    @JsonIgnore
    default Specification<T> getSpecification() {
        return new Specification<T>(){
            @Override
            public Predicate toPredicate(Root<T> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                //root.get("address")表示获取address这个字段名称
                List<Predicate> predicates = buildPredicates(root, criteriaQuery, criteriaBuilder);

                return criteriaQuery.where(predicates.toArray(new Predicate[predicates.size()])).getRestriction();
            }
        };
    }

    default QueryWrapper<T> buildOrders(QueryWrapper<T> qw){
        BaseBean<T> baseBean = (BaseBean)this;
        if(baseBean.orders!=null&&baseBean.orders.size()>0) {
            baseBean.getOrders().stream().forEach(orderItem ->{
                qw.orderBy(true,orderItem.isAsc(),getMpColumnName(orderItem.getColumn()));
            });
        }
        return qw;
    }

    default void buildOrders(Root<T> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder){
        BaseBean<T> baseBean = (BaseBean)this;
        if(baseBean.orders!=null&&baseBean.orders.size()>0) {
            List<Order> orderList = Lists.newArrayList();

            baseBean.getOrders().stream().forEach(orderItem ->{
                if(orderItem.isAsc()){
                    orderList.add(criteriaBuilder.asc(root.get(orderItem.getColumn())));
                }else{
                    orderList.add(criteriaBuilder.desc(root.get(orderItem.getColumn())));
                }
            });
            criteriaQuery.orderBy(orderList);
        }
    }

    /**获取分页查询信息*/
    @JsonIgnore
    default Page<T> getMpPage() {
        BaseBean<T> baseBean = (BaseBean)this;
        Page<T> page = new Page<>(baseBean.getCurrent(),baseBean.getSize());
        if(baseBean.orders!=null&&baseBean.orders.size()>0){
            List<com.baomidou.mybatisplus.core.metadata.OrderItem> ois = baseBean.getOrders().stream().map(orderItem -> {
                return new com.baomidou.mybatisplus.core.metadata.OrderItem(getMpColumnName(orderItem.getColumn()),orderItem.isAsc());
            }).collect(Collectors.toList());
            page.setOrders(ois);
        }

        return page;
    }

    /**获取分页查询信息,页码从 1 开始，与 plus一致*/
    @JsonIgnore
    default org.springframework.data.domain.Pageable getJpaPage() {
        BaseBean<T> baseBean = (BaseBean)this;
        List<Sort.Order> orderList = Lists.newArrayList();
        if(baseBean.orders!=null&&baseBean.orders.size()>0){
            baseBean.orders.forEach(item ->{
                if(org.apache.commons.lang3.StringUtils.isNotEmpty(item.getColumn())){
                    orderList.add(new Sort.Order(item.isAsc()? Sort.Direction.ASC: Sort.Direction.DESC,getMpColumnName(item.getColumn())));
                }
            });
        }
        if( baseBean.getCurrent() == 0 ){
            baseBean.setCurrent(1);
        }
        return PageRequest.of(baseBean.getCurrent()-1,baseBean.getSize(), Sort.by(orderList));
    }

    /**mybatis 转义实体字段为数据库字段*/
    @JsonIgnore
    default String getMpColumnName(String fieldName){
        if(!StringUtils.isNotEmpty(fieldName)){
            return fieldName;
        }
        try {
            Field field = this.getClass().getSuperclass().getDeclaredField(fieldName);
            if(field != null){
                TableField tf = this.getClass().getSuperclass().getDeclaredField(fieldName).getAnnotation(TableField.class);
                if(tf != null){
                    return tf.value();
                }
            }

        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        return StringUtils.camelToUnderline(fieldName);
    }

    /**jpa 转义实体字段为数据库字段*/
    @JsonIgnore
    default Path<Object> getJpaColumnName(Root<T> root, String fieldName){
        return root.get(fieldName);
    }
}
