package employee;

import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebMvc
@ComponentScan("employee")
@PropertySource(value = "file:./employee.properties2", ignoreResourceNotFound = true)
public class SpringConfig implements WebMvcConfigurer {

    // Not Spring Boot, so @Value placeholder resolution needs this bean explicitly;
    // must be static so it runs early enough in the bean lifecycle (see Spring docs
    // for @Bean PropertySourcesPlaceholderConfigurer).
    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    // DispatcherServlet is mapped at "/" (WebInitializer), which otherwise shadows
    // Jetty's own static-file serving for src/main/webapp/*.html — delegate anything
    // Spring has no @RequestMapping for back to the container's default servlet.
    @Override
    public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
        configurer.enable();
    }

}
