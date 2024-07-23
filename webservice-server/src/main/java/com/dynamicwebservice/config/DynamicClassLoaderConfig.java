package com.dynamicwebservice.config;

import com.dynamicwebservice.util.DynamicClassLoader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.URL;

@Configuration
public class DynamicClassLoaderConfig {

    @Bean
    public DynamicClassLoader dynamicClassLoader() {
        URL[] urls = {};
        return new DynamicClassLoader(urls, this.getClass().getClassLoader());
    }
}
