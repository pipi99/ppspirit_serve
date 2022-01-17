package pp.spirit.base.config;

import org.hibernate.validator.HibernateValidator;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Role;
import org.springframework.core.annotation.Order;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;

import javax.validation.Validation;
import javax.validation.Validator;

/**
 * @author LiV
 * @Title:
 * @Package com.liv.web.config
 * @Description: 校验配置工具
 * @date 2020.4.16  18:08
 * @email 453826286@qq.com
 */
@Configuration
@Role(BeanDefinition.ROLE_INFRASTRUCTURE) //is not eligible for getting processed by all BeanPostProcessors
public class ValidateConfig {

    @Bean
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    public MethodValidationPostProcessor methodValidationPostProcessor() {
        MethodValidationPostProcessor postProcessor = new MethodValidationPostProcessor();
        postProcessor.setValidator(validator());
        return postProcessor;
    }

    @Bean
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    public Validator validator(){
        return Validation.byProvider( HibernateValidator.class ).configure()
        /**
         *1、普通模式（默认是这个模式）
         * 　　普通模式(会校验完所有的属性，然后返回所有的验证失败信息)
         *
         * 返回目录
         * 2、快速失败返回模式
         * 　　快速失败返回模式(只要有一个验证失败，则返回)
         */
        .addProperty( "hibernate.validator.fail_fast", "false" )
        .buildValidatorFactory().getValidator();
    }
}
