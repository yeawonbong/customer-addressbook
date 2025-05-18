package com.hyundai.test.address.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Slf4j
@Component
public class ApiLoggingInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        log.info("\n" +
                        "-------------------- [API Request] --------------------\n" +
                        "METHOD : {}\n" +
                        "URI    : {}\n" +
                        "-------------------------------------------------------",
                request.getMethod(),
                request.getRequestURI()
        );
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        log.info("\n" +
                        "-------------------- [API Response] -------------------\n" +
                        "METHOD : {}\n" +
                        "URI    : {}\n" +
                        "STATUS : {}\n" +
                        "-------------------------------------------------------",
                request.getMethod(),
                request.getRequestURI(),
                response.getStatus()
        );
    }
}
