package pp.spirit.io.util;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author LiV
 * @Title:
 * @Package com.pp.api.io.util
 * @Description: miniio配置信息
 * @date 2021.4.21  16:28
 * @email 453826286@qq.com
 */
@Data
@Component
public class IoProp {
    @Value("${pp.io.minio.endpoint}")
    private String endpoint;
    @Value("${pp.io.minio.access-key}")
    private String  accessKey;
    @Value("${pp.io.minio.secret-key}")
    private String  secretKey;
    @Value("${pp.io.minio.default-bucket}")
    private String  defaultBucket;
    @Value("${pp.io.type}")
    private String  type;
}
