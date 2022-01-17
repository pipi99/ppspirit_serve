package pp.spirit.base.config;

import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.BlockAttackInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import pp.spirit.base.interceptor.MyBatisPlusDynamicTableNameInterceptor;

@Configuration
@EnableTransactionManagement
public class MybatisPlusConfig {

    /**
     * @Description:
     * 注意:
     *
     * 使用多个功能需要注意顺序关系
     *
     * 多租户,动态表名
     * 分页,乐观锁
     * sql性能规范,防止全表更新与删除
     *
     * @date 2021.11.19 11:13
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        //动态表名
        interceptor.addInnerInterceptor(new MyBatisPlusDynamicTableNameInterceptor().intercept());
        //分页
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor());
        //sql性能规范,防止全表更新与删除
        interceptor.addInnerInterceptor(new BlockAttackInnerInterceptor());
        return interceptor;
    }
}
