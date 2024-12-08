package com.kwang.board.global.exception.exceptions;

import com.kwang.board.global.exception.exceptions.comment.CommentNotFoundException;
import com.kwang.board.global.exception.exceptions.photo.FileUploadException;
import com.kwang.board.global.exception.exceptions.post.PostNotFoundException;
import com.kwang.board.global.exception.exceptions.user.UserNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Map;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UnauthorizedAccessException.class)
    public ResponseEntity<Map<String, String>> handleUnauthorizedAccess(UnauthorizedAccessException e) {
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(Map.of("message", e.getMessage()));
    }

    @ExceptionHandler({PostNotFoundException.class, UserNotFoundException.class, CommentNotFoundException.class})
    public String handleNotFoundException(RuntimeException e, Model model) {
        model.addAttribute("errorMessage", e.getMessage());
        return "error/not-found";
    }

    @ExceptionHandler(FileUploadException.class)
    public String handleFileUpload(FileUploadException e, Model model,
                                   HttpServletRequest request,
                                   RedirectAttributes redirectAttributes) {
        String referer = request.getHeader("Referer");

        // 세션에서 작성 내용 가져오기 - 수정 페이지
        if (referer != null && referer.contains("/edit")) {
            String postId = referer.substring(referer.indexOf("/post/") + 6, referer.indexOf("/edit"));
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            redirectAttributes.addFlashAttribute("request",
                    request.getSession().getAttribute("postEditRequest"));
            return "redirect:/post/" + postId + "/edit";  // 게시글 ID를 포함한 수정 페이지로 리다이렉트
        }

        // 작성 페이지인 경우
        redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        redirectAttributes.addFlashAttribute("request",
                request.getSession().getAttribute("postCreateRequest"));
        return "redirect:/post/write";
    }

    @ExceptionHandler(RuntimeException.class)
    public String handleRuntimeException(RuntimeException e, Model model) {
        model.addAttribute("errorMessage", "예상치 못한 오류가 발생했습니다.");
        return "error/server-error";
    }
}
