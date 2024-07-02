package com.dynamicwebservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController(contextPath + "/**").setViewName("forward:/index.html");
    }
}
