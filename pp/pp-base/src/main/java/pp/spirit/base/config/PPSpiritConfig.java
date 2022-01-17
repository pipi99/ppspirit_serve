package pp.spirit.base.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * @author LW
 * @title: PPSpiritBase
 * @projectName spirit
 * @description: 供项目扫描包路径使用,
 * 本类负责扫描 pp.spirit
 * @date 2021.11.2011:25
 */
@SuppressWarnings("ALL")
@Configuration
@ComponentScan(basePackages = {
        "pp.spirit.base",
        "pp.spirit.io",
        "pp.spirit.util",
        "pp.spirit.cache",
        "pp.spirit.monitor",
        "pp.spirit.security",
        "pp.spirit.flow",
})
@MapperScan({
        "pp.spirit.io.dao.mapper",
        "pp.spirit.security.dao.mapper",
        "com.**.dao.mapper"
})
@EnableJpaRepositories(basePackages = {
        "pp.spirit.io.dao.jpa",
        "pp.spirit.security.dao.jpa",
        "com.**.dao.jpa"
})
@EntityScan(basePackages = {
        "pp.spirit.io.pojo",
        "pp.spirit.security.pojo",
        "com.**.pojo"
})
public class PPSpiritConfig {
}
