package com.kimsalee.jimcarry.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("https://hackathon-7nsg.vercel.app", "http://localhost:3000")
                .allowedMethods("GET","POST","PUT","DELETE","OPTIONS");
    }
}