package pp.spirit.cache.config;

import lombok.Data;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

/**
 * @author LiV
 * @Title:
 * @Package com.liv.shiro
 * @Description: redis配置，因为包spring-boot-starter-data-redis自身依赖lettuce,
 * 所以缺省情况下，LettuceConnectionConfiguration会生效,JedisConnectionConfiguration不生效
 * @date 2020.4.25  14:59
 * @email 453826286@qq.com
 */
@SuppressWarnings("ALL")
@Data
@Configuration
public class RedisConfig extends CachingConfigurerSupport {
    public static final String STANDALONE = "standalone";
    public static final String CLUSTER = "cluster";

    @Value("${redis.active}")
    private String active;

    @Autowired
    RedisConfigStandaloneProp redisConfigStandalone; //单实例的配置，读取yml文件

    @Autowired
    RedisConfigClusterProp redisConfigCluster; //集群的配置，读取yml文件

    /************集群的配置--start*******************/
    //读取pool配置
    @Bean
    public GenericObjectPoolConfig redisPoolCluster() {
        GenericObjectPoolConfig config = new GenericObjectPoolConfig();
        config.setMinIdle(redisConfigCluster.getMinIdle());
        config.setMaxIdle(redisConfigCluster.getMaxIdle());
        config.setMaxTotal(redisConfigCluster.getMaxTotal());
        config.setMaxWait(Duration.ofMillis(redisConfigCluster.getMaxWait()));
        return config;
    }

    @Bean
    public RedisClusterConfiguration redisConfigCluster() {//集群配置类
        RedisClusterConfiguration redisConfig = new RedisClusterConfiguration(redisConfigCluster.getNodes());
        redisConfig.setMaxRedirects(redisConfigCluster.getMaxRedirects());
        redisConfig.setPassword(RedisPassword.of(redisConfigCluster.getPassword()));
        return redisConfig;
    }
    
    @Bean
    @ConditionalOnProperty(prefix = "redis",name = "active",havingValue = "cluster")
    public LettuceConnectionFactory factoryCluster(@Qualifier("redisPoolCluster") GenericObjectPoolConfig config,@Qualifier("redisConfigCluster") RedisClusterConfiguration redisConfig) {
        LettuceClientConfiguration clientConfiguration = LettucePoolingClientConfiguration.builder().poolConfig(config).build();
        return new LettuceConnectionFactory(redisConfig, clientConfiguration);
    }

    /**
     * 集群redis数据源
     * @param connectionFactory
     * @return
     */
    @Bean
    @ConditionalOnProperty(prefix = "redis",name = "active",havingValue = "cluster")
    public RedisTemplate<String, Object> redisTemplateCluster(@Qualifier("factoryCluster")LettuceConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        ObjectByteRedisSerializer serializer = new ObjectByteRedisSerializer();
        //使用StringRedisSerializer来序列化和反序列化redis的key值
        StringRedisSerializer ser = new StringRedisSerializer();
        template.setKeySerializer(ser);
        template.setHashKeySerializer(ser);
        template.setHashValueSerializer(serializer);
        template.setValueSerializer(serializer);
        template.afterPropertiesSet();
        return template;
    }
    /************集群的配置--end*******************/

    /************单实例的配置--start*******************/
    //读取pool配置
    @Bean
    public GenericObjectPoolConfig redisPoolStandalone() {
        GenericObjectPoolConfig config = new GenericObjectPoolConfig();
        config.setMinIdle(redisConfigStandalone.getMinIdle());
        config.setMaxIdle(redisConfigStandalone.getMaxIdle());
        config.setMaxTotal(redisConfigStandalone.getMaxTotal());
        config.setMaxWait(Duration.ofMillis(redisConfigStandalone.getMaxWait()));
        return config;
    }

    @Bean
    public RedisStandaloneConfiguration redisConfigStandalone() {
        RedisStandaloneConfiguration redisConfig = new RedisStandaloneConfiguration(redisConfigStandalone.getHost(),redisConfigStandalone.getPort());
        redisConfig.setDatabase(redisConfigStandalone.getDatabase());
        redisConfig.setPassword(RedisPassword.of(redisConfigStandalone.getPassword()));
        return redisConfig;
    }

    @Bean
    @ConditionalOnProperty(prefix = "redis",name = "active",havingValue = "standalone")
    public LettuceConnectionFactory factoryStandalone(@Qualifier("redisPoolStandalone") GenericObjectPoolConfig config,@Qualifier("redisConfigStandalone") RedisStandaloneConfiguration redisConfig) {//注意传入的对象名和类型RedisStandaloneConfiguration
        LettuceClientConfiguration clientConfiguration = LettucePoolingClientConfiguration.builder().poolConfig(config).build();
        return new LettuceConnectionFactory(redisConfig, clientConfiguration);
    }


    /**
     * 单实例redis数据源
     *
     * @param connectionFactory
     * @return
     */
    @Bean
    @ConditionalOnProperty(prefix = "redis",name = "active",havingValue = "standalone")
    public RedisTemplate<String, Object> redisTemplateStandalone(@Qualifier("factoryStandalone")LettuceConnectionFactory connectionFactory) {//注意传入的对象名
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        ObjectByteRedisSerializer serializer = new ObjectByteRedisSerializer();
        //使用StringRedisSerializer来序列化和反序列化redis的key值
        StringRedisSerializer ser = new StringRedisSerializer();
        template.setKeySerializer(ser);
        template.setHashKeySerializer(ser);
        template.setHashValueSerializer(serializer);
        template.setValueSerializer(serializer);
        template.afterPropertiesSet();
        return template;
    }
    /************单实例的配置--end*******************/
}