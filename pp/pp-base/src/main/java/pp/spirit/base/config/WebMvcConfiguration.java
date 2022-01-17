package pp.spirit.base.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.context.request.RequestContextListener;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**s
 * @author LiV
 * @Title:
 * @Package com.liv.config
 * @Description: 全局配置
 * @date 2020.3.26  10:14
 * @email 453826286@qq.com
 */
@Configuration
public class WebMvcConfiguration extends WebMvcConfigurationSupport {

    /**
     * @return void
     * @Description 全局fastJson替换
     * @Param [converters]
     */
//    @Override
//    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
//        super.configureMessageConverters(converters);
//        converters.add(fastJsonHttpMessageConverter());
//        converters.add(responseBodyConverter());
//    }
//    @Bean
//    public FastJsonHttpMessageConverter fastJsonHttpMessageConverter() {
//        FastJsonHttpMessageConverter fastConverter = new FastJsonHttpMessageConverter();
//        FastJsonConfig fastJsonConfig = new FastJsonConfig();
//        // 配置序列化策略
//        fastJsonConfig.setSerializerFeatures(
//                SerializerFeature.PrettyFormat,
//                SerializerFeature.WriteNullListAsEmpty,
//                SerializerFeature.WriteMapNullValue,
//                SerializerFeature.WriteEnumUsingToString,
//                SerializerFeature.WriteNullStringAsEmpty,
//                SerializerFeature.BrowserCompatible);
//
//        fastJsonConfig.setDateFormat("yyyy-MM-dd HH:mm:ss");
//        List<MediaType> fastMediaTypes = new ArrayList<>();
//        fastMediaTypes.add(MediaType.APPLICATION_JSON);
//        fastConverter.setSupportedMediaTypes(fastMediaTypes);
//        fastConverter.setFastJsonConfig(fastJsonConfig);
//        return fastConverter;
//    }

    //jackson 强制无默认构造报错
    /**
     * @return void
     * @Description 全局jackson替换
     * @Param [converters]
     */
    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        super.configureMessageConverters(converters);
        converters.add(getMappingJackson2HttpMessageConverter());
        converters.add(responseBodyConverter());
    }
    @Bean
    public MappingJackson2HttpMessageConverter getMappingJackson2HttpMessageConverter() {
        MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter = new MappingJackson2HttpMessageConverter();

        ObjectMapper objectMapper = new ObjectMapper();

        //设置日期格式
        SimpleDateFormat smt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        objectMapper.setDateFormat(smt);

        //针对于JDK新时间类。序列化时带有T的问题，自定义格式化字符串
        JavaTimeModule javaTimeModule = new JavaTimeModule();
        javaTimeModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        javaTimeModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        objectMapper.registerModule(javaTimeModule);

        //若POJO中不含有JSON中的属性，则抛出异常。开启后，不解析该字段，而不会抛出异常。
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        //默认关闭，当JSON字段为""(EMPTY_STRING)时，解析为普通的POJO对象抛出异常。开启后，该POJO的属性值为null。
        objectMapper.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT,true);

        //long类型精度丢失问题
        SimpleModule simpleModule = new SimpleModule();
        simpleModule.addSerializer(Long.class, ToStringSerializer.instance);
        simpleModule.addSerializer(Long.TYPE, ToStringSerializer.instance);
        objectMapper.registerModule(simpleModule);

        //json配置
        mappingJackson2HttpMessageConverter.setObjectMapper(objectMapper);

        //设置中文编码格式
        List<MediaType> list = new ArrayList<MediaType>();
        list.add(MediaType.APPLICATION_JSON);
        mappingJackson2HttpMessageConverter.setSupportedMediaTypes(list);
        return mappingJackson2HttpMessageConverter;
    }

    @Override
    protected void addInterceptors(InterceptorRegistry registry) {
        // excludePathPatterns("/swagger-resources/**","/webjars/**","/swagger-ui/**","/v3/**")
    }

    @Override
    protected void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 解决静态资源无法访问
        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/META-INF/resources/")
                .addResourceLocations("classpath:/resources/")
                .addResourceLocations("classpath:/static/")
                .addResourceLocations("classpath:/public/");

        // 解决swagger无法访问
        registry.addResourceHandler("/swagger-ui/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/springfox-swagger-ui/");
        // 解决swagger的js文件无法访问
        registry.addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/");
        super.addResourceHandlers(registry);
    }

    @Bean
    public HttpMessageConverter responseBodyConverter() {
        StringHttpMessageConverter converter = new StringHttpMessageConverter(Charset.forName("UTF-8"));
        return converter;
    }

    /**
     * 监听器：监听HTTP请求事件
     * 解决RequestContextHolder.getRequestAttributes()空指针问题
     * @return
     */
    @Bean
    public RequestContextListener requestContextListener(){
        return new RequestContextListener();
    }
}
