package pp.spirit.base.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

/**
 * @author LiV
 * @Title:
 * @Package com.liv.web.config
 * @Description: SwaggerUI配置 , 后台支撑主要为web开发 ，swagger的拦截这里 为 basic 拦截 ， 不需要输入 token
 * @date 2020.4.15  16:23
 * @email 453826286@qq.com
 */
@Configuration
public class SwaggerConfig {
    @Bean
    public Docket createRestApi() {
        return new Docket(DocumentationType.OAS_30)
                .groupName("PPSpirit")
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("pp.spirit"))
                .paths(PathSelectors.any())
                .build();
    }

    @Bean
    public Docket createPPSpiritApi() {
        return new Docket(DocumentationType.OAS_30)
                .groupName("my")
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com"))
                .paths(PathSelectors.any())
                .build();
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("Swagger RESTful APIs")
                .description("webDesk Swagger API 服务")
                .termsOfServiceUrl("/swagger-ui/index.html")
                .contact(new Contact("liv", "", "livsina@sina.com"))
                .version("1.0")
                .build();
    }
}
