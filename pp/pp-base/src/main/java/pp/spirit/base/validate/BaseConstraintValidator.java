package pp.spirit.base.validate;

import pp.spirit.base.utils.ThreadLocalContextHolder;
import pp.spirit.base.validate.annotation.BaseConstraint;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.lang.reflect.InvocationTargetException;

/**
 * @author LiV
 * @Title:
 * @Package com.liv.api.base.validate
 * @Description: 调用自定义的校验规则
 * @date 2021.4.13  10:51
 * @email 453826286@qq.com
 */
public  class BaseConstraintValidator implements ConstraintValidator<BaseConstraint,Object> {
    /**
     * @Description: 执行校验方法
     * @date 2021.11.20 20:59
     */
    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        ThreadLocalContextHolder.set(context);
        try {
            return (boolean) value.getClass().getMethod("doValid",Object.class).invoke(value,value);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return false;
    }
}
