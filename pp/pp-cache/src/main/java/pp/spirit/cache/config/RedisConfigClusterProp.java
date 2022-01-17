package pp.spirit.cache.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

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
@ConfigurationProperties(prefix="redis.cluster")
public class RedisConfigClusterProp {
    // Redis数据库索引（默认为0）
    @Value("${redis.cluster.database}")
    private Integer database;
    // Redis服务器连接密码（默认为空）
    @Value("${redis.cluster.password}")
    private String password;
    // 连接超时时间（毫秒）
    @Value("${redis.cluster.timeout}")
    private Integer timeout;

    // 连接池最大连接数（使用负值表示没有限制）
    @Value("${redis.cluster.lettuce.pool.max-active}")
    private Integer maxTotal;
    // 连接池最大阻塞等待时间（使用负值表示没有限制）
    @Value("${redis.cluster.lettuce.pool.max-wait}")
    private Integer maxWait;
    // 连接池中的最大空闲连接
    @Value("${redis.cluster.lettuce.pool.max-idle}")
    private Integer maxIdle;
    // 连接池中的最小空闲连接
    @Value("${redis.cluster.lettuce.pool.min-idle}")
    private Integer minIdle;
    // 关闭超时时间
    @Value("${redis.cluster.lettuce.shutdown-timeout}")
    private Integer shutdown;

    //cluster映射
    List<String> nodes; //@ConfigurationProperties(prefix="redis.cluster")映射

    @Value("${redis.cluster.max-redirects:3}")
    private int maxRedirects;
}
