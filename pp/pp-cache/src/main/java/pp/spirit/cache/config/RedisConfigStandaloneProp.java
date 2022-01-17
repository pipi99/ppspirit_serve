package pp.spirit.cache.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author LiV
 * @Title:
 * @Package com.liv.shiro
 * @Description:  单实例配置
 * @date 2020.4.25  14:59
 * @email 453826286@qq.com
 */
@Data
@Component
@ConfigurationProperties(prefix="redis.single")
public class RedisConfigStandaloneProp {
    // Redis服务器地址
    @Value("${redis.single.host}")
    private String host;
    // Redis服务器连接端口
    @Value("${redis.single.port}")
    private Integer port;
    // Redis数据库索引（默认为0）
    @Value("${redis.single.database}")
    private Integer database;
    // Redis服务器连接密码（默认为空）
    @Value("${redis.single.password}")
    private String password;
    // 连接超时时间（毫秒）
    @Value("${redis.single.timeout}")
    private Integer timeout;

    // 连接池最大连接数（使用负值表示没有限制）
    @Value("${redis.single.lettuce.pool.max-active}")
    private Integer maxTotal;
    // 连接池最大阻塞等待时间（使用负值表示没有限制）
    @Value("${redis.single.lettuce.pool.max-wait}")
    private Integer maxWait;
    // 连接池中的最大空闲连接
    @Value("${redis.single.lettuce.pool.max-idle}")
    private Integer maxIdle;
    // 连接池中的最小空闲连接
    @Value("${redis.single.lettuce.pool.min-idle}")
    private Integer minIdle;
    // 关闭超时时间
    @Value("${redis.single.lettuce.shutdown-timeout}")
    private Integer shutdown;
}
