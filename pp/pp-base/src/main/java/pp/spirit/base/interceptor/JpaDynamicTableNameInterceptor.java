package pp.spirit.base.interceptor;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.resource.jdbc.spi.StatementInspector;

/**
 * @author LiV
 * @Title:
 * @Package com.liv.api.base.interceptor
 * @Description: jpa 表前缀拼截
 * @date 2021.2.25  09:51
 * @email 453826286@qq.com
 */
@Slf4j
public class JpaDynamicTableNameInterceptor implements StatementInspector {
    @Override
    public String inspect(String s) {
        return TableNameHandler.handleTableName(s);
    }


}
