package com.kwang.board.global.exception;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class CustomErrorController implements ErrorController {
    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, Model model) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);

        if (status != null) {
            int statusCode = Integer.parseInt(status.toString());

            if (statusCode == HttpStatus.NOT_FOUND.value()) {
                model.addAttribute("errorMessage", "요청하신 페이지를 찾을 수 없습니다.");
                return "error/not-found";
            } else if (statusCode == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
                model.addAttribute("errorMessage", "서버 오류가 발생했습니다.");
                return "error/server-error";
            } else {
                model.addAttribute("errorMessage", "요청을 처리할 수 없습니다.");
                return "error/client-error";  // 4xx 에러를 위한 공통 페이지
            }
        }

        model.addAttribute("errorMessage", "서버 오류가 발생했습니다.");
        return "error/server-error";
    }
}
