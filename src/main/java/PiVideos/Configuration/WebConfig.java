package PiVideos.Configuration;


import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new SessionInterceptor())
                .addPathPatterns("/**")
                .excludePathPatterns("/css/**", "/js/**","/login","/register","/error"); // Seiten, die frei zugänglich sein sollen
    }

     @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry
            .addResourceHandler("/videos/**") 
            .addResourceLocations("file:/home/berry/videostorage/"); 
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")  
                .allowedOrigins("http://piwacheserver:8080")  
                .allowedMethods("GET", "POST", "PUT", "DELETE")  
                .allowCredentials(true)  
                .allowedHeaders("*")  
                .exposedHeaders("Authorization");  
    }
}
