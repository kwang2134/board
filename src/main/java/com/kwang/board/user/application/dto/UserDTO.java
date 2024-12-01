package com.kwang.board.user.application.dto;

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
        private String loginId;
        private String password;
        private String username;
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
