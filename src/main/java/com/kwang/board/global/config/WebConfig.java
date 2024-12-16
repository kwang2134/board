package com.kwang.board.global.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.resource.PathResourceResolver;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/images/**")
                .addResourceLocations("file:///D:/project/images/")
                .setCachePeriod(3600)
                .resourceChain(true)
                .addResolver(new PathResourceResolver());

        registry.addResourceHandler("/css/**")
                .addResourceLocations("classpath:/static/css/");

        registry.addResourceHandler("/js/**")
                .addResourceLocations("classpath:/static/js/");

        /*registry.addResourceHandler("/images/**")
                .addResourceLocations("classpath:/static/images/");*/
    }
}
