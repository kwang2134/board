package com.kwang.board.user.adapters.controller.user;

import com.kwang.board.user.application.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserApiController {

    private final UserService userService;


    @GetMapping("/check-loginId")
    public ResponseEntity<?> checkLoginId(@RequestParam String loginId) {
        log.info("check-loginId = {}", loginId);
        Map<String, Object> response = new HashMap<>();

        if (loginId == null || loginId.trim().isEmpty()) {
            response.put("available", false);
            response.put("message", "아이디를 입력해주세요.");
            return ResponseEntity.badRequest().body(response);
        }

        boolean isAvailable = userService.isLoginIdAvailable(loginId);

        response.put("available", isAvailable);
        response.put("message", isAvailable ? "사용 가능한 아이디입니다." : "이미 사용중인 아이디입니다.");

        return ResponseEntity.ok(response);
    }
}
