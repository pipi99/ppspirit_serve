package pp.spirit.security.springsecurity.config;


import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Configuration;
import pp.spirit.security.springsecurity.utils.SecurityAuthenticationCacheUtil;

@Configuration
@Slf4j
public class ApplicationRunnerImpl implements ApplicationRunner {
    @Override
    public void run(ApplicationArguments args) throws Exception {
        clearCache();
    }

    /**
    * @Description: 清除系统缓存
    * @author: liv
    * @date  2022.1.14 16:13
    */
    private void clearCache(){
        log.debug("--清除缓存信息--");
        SecurityAuthenticationCacheUtil.deleteAllCache();
    }
}