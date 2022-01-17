package com.pp.pojo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.validator.constraints.Length;
import pp.spirit.base.base.BaseBean;
import pp.spirit.base.incrementer.IdGenerator;
import pp.spirit.base.validate.ValidException;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author LiV
 * @Title:
 * @Package com.liv.sp.dao.datamodel
 * @Description:
 *
 * 数据库实体,jpa和mybatis共用
 *
 * 1、表名和字段名称均使用驼峰标识，对应数据库使用下划线分割,指定 catalog（jpa）。
 * 2、主键采用自定义雪花ID算法,mybatis 默认设置，jpa需要GeneratedValue+GenericGenerator 配置
 * 3、Entity同时提供给 jpa 和 mybatis 使用 ，对二者注解的使用有一定功底
 * 4、采用 hibernateValidator 校验
 * 5、单表增删改查使用 mybatis
 * 6、联合查询使用 jpa
 * 7、not sql
 *
 *
 * @date 2020.12.11  10:52
 * @email 453826286@qq.com
 */
@Data
@Entity
@Table(name = "SP")
@TableName(value = "SP")
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="sp", description="java bean demo")
public class Sp  extends BaseBean<Sp> implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @TableId
    @GeneratedValue(generator = IdGenerator.GEN_NAME,strategy = GenerationType.SEQUENCE)
    @GenericGenerator(name = IdGenerator.GEN_NAME, strategy = IdGenerator.GEN_CLASS_NAME)
    @ApiModelProperty(value = "主键")
    private Long id ;

    /*名称*/
    @ApiModelProperty(value = "名称")
    @Column(name="T_NAME")
    @TableField("T_NAME")
    private String name;

    /*说明*/
    @ApiModelProperty(value = "描述")
    private String remark;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @Override
    public void valid(Sp sp){
        //执行校验
        if (StringUtils.isEmpty(name)) {
            throw new ValidException("名称不能为空");
        }
    }
}
