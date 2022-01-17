package pp.spirit.base.validate.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author LiV
 * @Title:
 * @Package com.liv.web.annotation
 * @Description: 处理校验结果
 * @date 2020.4.19  14:37
 * @email 453826286@qq.com
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@java.lang.annotation.Documented
public @interface ValidResult {
}
