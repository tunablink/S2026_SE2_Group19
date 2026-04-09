package com.example.cybersec.common.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC configuration — static resource handler cho Compete UI assets.
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/compete-static/**")
                .addResourceLocations("classpath:/templates/compete/styles/");

        registry.addResourceHandler("/compete-imports/**")
                .addResourceLocations("classpath:/templates/compete/imports/");
    }
}
