package pp.spirit.base.validate.annotation;

import org.springframework.core.annotation.AliasFor;
import pp.spirit.base.validate.BaseConstraintValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
 * @author LiV
 * @Title:
 * @Package com.liv.api.base.validate
 * @Description: 自定义校验注解
 * @date 2021.4.13  10:42
 * @email 453826286@qq.com
 */
@Documented
@Target({ ElementType.TYPE,ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Constraint(validatedBy = BaseConstraintValidator.class)
public @interface BaseConstraint {

    //重写元注解
    // 实际运行的时候会调用此校验实现
    @AliasFor(annotation = Constraint.class, attribute = "validatedBy")
    Class<? extends javax.validation.ConstraintValidator<?,?>>[] by();
    /**
     * 配置message信息
     * @return
     */
    String message() default "违规参数";

    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}