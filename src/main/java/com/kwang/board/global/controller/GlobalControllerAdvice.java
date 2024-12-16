package com.kwang.board.global.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

/**
 * Thymeleaf 3.1 이후 보안상 이유로 #request, #session 에 직접적인 접근 불가로 우회하여 접근
 */

@ControllerAdvice
public class GlobalControllerAdvice {

    @ModelAttribute("currentUri")
    public String currentUri(HttpServletRequest request) {
        return request.getRequestURI();
    }
}
