package id.ac.ui.cs.advprog.mysawit.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins(
                        "http://localhost:3000",
                        "https://my-sawit-frontend.vercel.app")
                .allowedHeaders("Authorization", "Content-Type") // allow fe to send token     
                .exposedHeaders("Authorization"); // allow fe to read token
    }
}  
