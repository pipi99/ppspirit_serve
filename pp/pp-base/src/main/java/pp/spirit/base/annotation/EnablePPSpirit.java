package pp.spirit.base.annotation;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import pp.spirit.base.config.PPSpiritConfig;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Retention(value = java.lang.annotation.RetentionPolicy.RUNTIME)
@Target(value = { java.lang.annotation.ElementType.TYPE })
@Documented
@Import(PPSpiritConfig.class)
public @interface EnablePPSpirit {
}
