//package my;
//
//import org.springframework.boot.Banner;
//import org.springframework.boot.SpringApplication;
//import org.springframework.boot.autoconfigure.SpringBootApplication;
//import org.springframework.boot.builder.SpringApplicationBuilder;
//import org.springframework.boot.context.metrics.buffering.BufferingApplicationStartup;
//import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
//import pp.spirit.base.annotation.EnablePPSpirit;
//
///**
// * @Title: App
// * @Description: TODO(这里用一句话描述这个方法的作用)
// * @author LW
// * @date 2021.11.19 15:42
// */
//
//@SpringBootApplication(scanBasePackages = "com")
//@EnablePPSpirit
//public class App extends SpringBootServletInitializer {
//    public static void main(String[] args) {
//        SpringApplication application = new SpringApplication(App.class);
//        application.setBannerMode(Banner.Mode.OFF);
//        application.setApplicationStartup(new BufferingApplicationStartup(2048));
//        application.run(args);
//    }
//
//    @Override
//    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
//        return application.sources(App.class);
//    }
//}
