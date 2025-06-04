package PiVideos.Configuration;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

 
    //SessionInterceptor leitet alles auf Login seite weiter außer angegebene Seiten
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new SessionInterceptor())
                .addPathPatterns("/**")
                .excludePathPatterns("/*.css", "/*.js", "/login", "/register", "/error"); // Seiten, die frei zugänglich sind
    }
    
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
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
