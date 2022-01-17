package pp.spirit.security.springsecurity.config;

import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.ObjectPostProcessor;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import pp.spirit.base.properties.PPProperties;
import pp.spirit.security.springsecurity.authentication.UsernamePasswordCaptchaProvider;
import pp.spirit.security.springsecurity.decision.UrlAccessDecisionManager;
import pp.spirit.security.springsecurity.decision.UrlFilterInvocationSecurityMetadataSource;
import pp.spirit.security.springsecurity.entrypoint.JwtAuthenticationEntryPoint;
import pp.spirit.security.springsecurity.filter.JwtAuthenticationTokenFilter;
import pp.spirit.security.springsecurity.handler.JwtAccessDeniedHandler;
import pp.spirit.security.springsecurity.handler.LoginFailureHandler;
import pp.spirit.security.springsecurity.handler.LoginSuccessHandler;
import pp.spirit.security.springsecurity.handler.LogoutSuccessHandler;
import pp.spirit.security.springsecurity.service.UserDetailsServiceImpl;
import pp.spirit.security.springsecurity.utils.JwtTokenUtil;

import javax.annotation.Resource;

@Configuration
public class WebSecurityConfig {

    @Resource
    private PPProperties ppProperties;

    @Resource
    private BCryptPasswordEncoder passwordEncoder;

    // springsecurity UserDetailsService
    @Resource
    private UserDetailsServiceImpl userDetailsService;

    @Resource
    private UrlFilterInvocationSecurityMetadataSource urlFilterInvocationSecurityMetadataSource;

    @Resource
    private UrlAccessDecisionManager urlAccessDecisionManager;

    /**
     * 匿名访问拦截过滤器
     */
    @Configuration
    @Order(1)
    public class AnonymousWebSecurityConfig extends WebSecurityConfigurerAdapter {
        /**
         * 影响全局安全性的配置设置（忽略资源，设置调试模式，通过实现自定义防火墙定义拒绝请求）
         */
        @Override
        public void configure(WebSecurity web) throws Exception {
            //放行静态资源,全局生效
            web.ignoring()
                    //系统默认访问（自动跳转链接）
                    .antMatchers(HttpMethod.GET, "/","/error")
                    .antMatchers(HttpMethod.OPTIONS, "/**")
                    //系统默认提供的匿名访问链接配置
                    .antMatchers("/o/p/**")
                    //静态资源
                    .antMatchers("/static/**","/**/*.js","/**/*.png","/**/*.jpg","/**/*.gif","/**/*.jpeg","/**/*.css","/**/*.woff","/**/*.ttf","/**/*.ico","/**/*.eot","/**/*.svg","/**/*.MP4","/**/*.mp4");
        }
        @Override
        protected void configure(HttpSecurity http) throws Exception {
            //匿名访问链接处理
            String anonymousPath = ppProperties.getSecurityAnonymousPath();
            if(StringUtils.isNotEmpty(anonymousPath)){
                http.requestMatchers(requestMatcherConfigurer -> {
                    //本SecurityFilterChain 进入的条件
                    requestMatcherConfigurer.antMatchers(anonymousPath.split(","));
                }).anonymous();
            }
        }
    }
    /**
     * Basic认证方式过滤器
     * 用于开发阶段，调用swagger-ui的时候鉴权
     * 优先拦截权限
     */
    @Order(2)
    @Configuration
    @ConditionalOnProperty(value = "spring.profiles.active", havingValue = "dev")
    public class ApiHttpBasicWebSecurityConfig extends WebSecurityConfigurerAdapter {

        @Override
        protected void configure(AuthenticationManagerBuilder builder) throws Exception {
            //将erase-credentials设置为false后，不会清除这些保密信息
//            builder.eraseCredentials(false);
            //内存（一定要写在userservice之前）
            builder.inMemoryAuthentication()
                    .withUser("root")
                    .password(passwordEncoder.encode("root"))
                    .authorities("DEVELOPER");
            builder.userDetailsService(userDetailsService)
                    .passwordEncoder(passwordEncoder);
        }

        @Override
        protected void configure(HttpSecurity http) throws Exception {

            http
                //本SecurityFilterChain 进入的 条件
                .requestMatchers(requestMatcherConfigurer -> {
                    requestMatcherConfigurer.antMatchers("/**");
                })

                //除可匿名链接外，所有的请求都需要认证访问
                .authorizeRequests(authorizeRequest->{
                    authorizeRequest
                            .anyRequest()
                            .authenticated()
                            .withObjectPostProcessor(new ObjectPostProcessor<FilterSecurityInterceptor>() {
                        @Override
                        public <O extends FilterSecurityInterceptor> O postProcess(O o) {
                            o.setSecurityMetadataSource(urlFilterInvocationSecurityMetadataSource); //动态获取url权限配置
                            o.setAccessDecisionManager(urlAccessDecisionManager); //权限判断
                            return o;
                        }
                    });
                })
                .httpBasic()

                // 关闭csrt，或者添加key处理，否则 post请求无法访问
                .and()
                .csrf()
                .disable();

        }
    }

    /**
     * 需登录认证方式
     */
    @Configuration
    @EnableGlobalMethodSecurity(prePostEnabled = true,securedEnabled = true)
    public class FormLoginWebSecurityConfig extends WebSecurityConfigurerAdapter {

        @Resource
        private JwtTokenUtil jwtTokenUtil;

        /**
         * 自定义认证操作
         * @return 认证操作
         */
        @Bean
        public AuthenticationProvider usernamePasswordCaptchaProvider() {
            return new UsernamePasswordCaptchaProvider(userDetailsService,passwordEncoder);
        }

        @Override
        protected void configure(AuthenticationManagerBuilder builder) throws Exception {
            builder.authenticationProvider(usernamePasswordCaptchaProvider());
        }


        //允许根据选择匹配在 资源级别 配置基于Web的安全性
        /**
         * anyRequest          |   匹配所有请求路径
         * access              |   SpringEl表达式结果为true时可以访问
         * anonymous           |   匿名可以访问
         * denyAll             |   用户不能访问
         * fullyAuthenticated  |   用户完全认证可以访问（非remember-me下自动登录）
         * hasAnyAuthority     |   如果有参数，参数表示权限，则其中任何一个权限可以访问
         * hasAnyRole          |   如果有参数，参数表示角色，则其中任何一个角色可以访问
         * hasAuthority        |   如果有参数，参数表示权限，则其权限可以访问
         * hasIpAddress        |   如果有参数，参数表示IP地址，如果用户IP和参数匹配，则可以访问
         * hasRole             |   如果有参数，参数表示角色，则其角色可以访问
         * permitAll           |   用户可以任意访问
         * rememberMe          |   允许通过remember-me登录的用户访问
         * authenticated       |   用户登录后可访问
         */
        @Override
        public void configure(HttpSecurity http) throws Exception {

            http
                //本SecurityFilterChain 进入的 条件
                .requestMatchers(requestMatcherConfigurer -> {
                    requestMatcherConfigurer.antMatchers("/**");
                })

                // 所有请求全部需要鉴权认证
                .authorizeRequests(expressionInterceptUrlRegistry -> {
                    expressionInterceptUrlRegistry
                        .antMatchers( "/signin","/pp/auth/reg","/pp/auth/unlock/**","/kaptcha/**").permitAll()
                        .anyRequest()
                        .authenticated()
                        .withObjectPostProcessor(new ObjectPostProcessor<FilterSecurityInterceptor>() {
                            @Override
                            public <O extends FilterSecurityInterceptor> O postProcess(O o) {
                                o.setSecurityMetadataSource(urlFilterInvocationSecurityMetadataSource); //动态获取url权限配置
                                o.setAccessDecisionManager(urlAccessDecisionManager); //权限判断
                                return o;
                            }
                        });
                })

                // 登录配置
                .formLogin(httpSecurityFormLoginConfigurer -> {
                    httpSecurityFormLoginConfigurer
                        .successHandler(new LoginSuccessHandler())
                        .failureHandler(new LoginFailureHandler())
                        .loginProcessingUrl("/signin")
                        .usernameParameter("username")
                        .passwordParameter("password");
                })

                // 登出配置//注销的接口get方法
                .logout(httpSecurityLogoutConfigurer -> {
                    httpSecurityLogoutConfigurer
                        .logoutUrl("/signout")
                        //清除cookie
                        .logoutSuccessHandler(new LogoutSuccessHandler()).deleteCookies(jwtTokenUtil.getTOKEN_HEADER())
                        .permitAll();
                })

                // 访问异常配置
                .exceptionHandling(httpSecurityExceptionHandlingConfigurer -> {
                    httpSecurityExceptionHandlingConfigurer
                            .authenticationEntryPoint(new JwtAuthenticationEntryPoint())
                            // 自定义权限不足处理类
                            .accessDeniedHandler(new JwtAccessDeniedHandler());
                });


            //配置自己的jwt验证过滤器
            // 切记只能new 不要注册为SpringBean，否则会装载到spring容器级别的filter
            http.addFilterBefore(new JwtAuthenticationTokenFilter(), UsernamePasswordAuthenticationFilter.class);
            // disable page caching
            http.headers().cacheControl().disable();
            // 基于token，所以不需要session
            http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
            //跨域处理 ,禁用 csrf, httpBasic
            http.cors().and().csrf().disable();
        }
    }
 }
