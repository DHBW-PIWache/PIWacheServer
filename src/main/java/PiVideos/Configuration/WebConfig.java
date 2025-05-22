package PiVideos.Configuration;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/******************************************************************************************************* 
Autor: Julian Hecht
Datum letzte Änderung: 21.04.2025
Änderung: Kommentare hinzugefügt 
*******************************************************************************************************/

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    //SessionInterceptor leitet alles auf Login seite weiter außer angegebene Seiten
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new SessionInterceptor())
                .addPathPatterns("/**")
                .excludePathPatterns("/*.css", "/*.js", "/login", "/register", "/error"); // Seiten, die frei zugänglich sein sollen
    }
   

    //Cors fehler -> noch keine Auswirkung bis jetzt gehabt
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")  
                .allowedOrigins("http://piwacheserver:8080")  
                .allowedMethods("GET", "POST", "PUT", "DELETE")  
                .allowCredentials(true)  
                .allowedHeaders("*")  
                .exposedHeaders("Authorization");  
    }
    
    // Resource Mapper für die Videos, damit sie auf der Website integrierbar sind 
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry
            .addResourceHandler("/videos/**") 
            .addResourceLocations("file:C:/Users/hecht/Desktop/Videospiwacheserver/");
    }
                
}
