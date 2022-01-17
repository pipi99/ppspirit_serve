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

import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;
import java.lang.reflect.Field;
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

    /**组装查询条件*/
    @JsonIgnore
    public QueryWrapper<T> getQueryWrapper();

    /**组装查询条件*/
    @JsonIgnore
    public Specification<T> getSpecification();

    /**获取分页查询信息*/
    @JsonIgnore
    default Page<T> getMpPage() {
        BaseBean<T> baseBean = (BaseBean)this;
        Page<T> page = new Page<>(baseBean.getCurrent(),baseBean.getSize());
        if(baseBean.orders!=null&&baseBean.orders.size()>0){
            //转换实体类字段名称为数据库字段 名称
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
