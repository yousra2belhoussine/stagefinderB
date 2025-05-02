package com.stagefinder.security;

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
          .allowedOrigins("http://localhost:4200") // ✅ Spécifie clairement l'origine autorisée
          .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
          .allowedHeaders("*")
          .allowCredentials(true); // ✅ Autorise les cookies, headers d'auth…
      }
    };
  }
}
