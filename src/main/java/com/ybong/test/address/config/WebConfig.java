package com.ybong.test.address.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import com.ybong.test.address.common.ApiLoggingInterceptor;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new ApiLoggingInterceptor())
                .addPathPatterns("/api/**"); // 원하는 경로 지정
    }
}
