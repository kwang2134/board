package com.kwang.board.user.application.dto;

import com.kwang.board.global.validation.ValidationGroups;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class UserDTO {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Request {
        private Long id;

        @NotBlank(message = "아이디는 필수 입력 사항입니다", groups = {ValidationGroups.SignUp.class})
        private String loginId;

        @NotBlank(message = "비밀번호는 필수 입력 사항입니다", groups = {ValidationGroups.SignUp.class, ValidationGroups.Update.class})
        private String password;

        @NotBlank(message = "이름은 필수 입력 사항입니다", groups = {ValidationGroups.SignUp.class, ValidationGroups.Update.class})
        private String username;

        @Email(message = "올바른 이메일 형식이 아닙니다")
        private String email;
    }

    @Getter
    @AllArgsConstructor
    public static class Response {
        private final Long id;
        private final String loginId;
        private final String username;
        private final String email;
        private final String role;
    }

}
