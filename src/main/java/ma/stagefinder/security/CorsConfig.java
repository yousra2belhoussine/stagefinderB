package ma.stagefinder.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig {

  @Bean
  public WebMvcConfigurer corsConfigurer() {
    return new WebMvcConfigurer() {
      @Override
      public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
          .allowedOriginPatterns("*") // ✅ Autorise toutes les origines même avec credentials
          .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
          .allowedHeaders("*")
          .allowCredentials(true); // ✅ Nécessaire si tu envoies des cookies ou JWT
      }
    };
  }
}
