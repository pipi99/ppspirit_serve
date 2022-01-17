package pp.spirit.base.properties;

import lombok.Data;
import lombok.ToString;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Data
@ToString
@Configuration
@ConfigurationProperties(prefix = "pp")
public class PPProperties {
    /** 登录成功自动存储到cookie */
    @Value("${pp.token.auto-bind-cookie}")
    private Boolean tokenAutoBindCookie;
    /** Base64对该令牌进行编码 */
    @Value("${pp.token.secret-key}")
    private String tokenSecretKey;
    /** 令牌过期时间 此处单位分钟 */
    @Value("${pp.token.token-expires-minutes}")
    private Long tokenTokenExpiresMinutes;
    /** refreshToken令牌过期时间 此处单位分钟 */
    @Value("${pp.token.refresh-token-timeout-minutes}")
    private Long tokenRefreshTokenTimeoutMinutes;


    /** 允许匿名访问的链接 */
    @Value("${pp.security.anonymous-path}")
    private String securityAnonymousPath;

    /** 用户默认密码 */
    @Value("${pp.user.default-password}")
    private String userDefaultPassword;
    /** 用户登录失败重试次数 */
    @Value("${pp.user.login-fail-retry-times}")
    private Integer userLoginFailRetryTimes;
    /** 用户登录失败锁定时常 */
    @Value("${pp.user.login-fail-locked-minutes}")
    private Integer userLoginFailLockedMinutes;

    /**缓存配置*/
    @Value("${pp.cache.expires}")
    public Integer cacheExpires = 604800;   //单位分钟 默认缓存有效时长，7天，使用CacheUtils 简单cache无永久缓存，默认设置不用修改;如需永久，使用 RedisUtils

    /**数据库配置*/
    @Value("${pp.database.default-table-prefix}")
    public String databaseDefaultTablePrefix;   //表名称前缀

    //所有的数据库配置
    public Map<String,Object> database;
}
