package com.kwang.board.post.application.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class PostDTO {

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Request {
        private String title;
        private String content;
        private String type;
        private String displayName;  // 비회원인 경우 작성자명
        private String password;  // 비회원인 경우 비밀번호
    }

    @Getter
    @AllArgsConstructor
    public static class Response {
        private final Long id;
        private final String username;
        private final String title;
        private final String content;
        private final String createdAt;
        private final String updatedAt;
        private final int recomCount;
        private final int notRecomCount;
    }

    @Getter
    @AllArgsConstructor
    public static class ListResponse {
        private final Long id;
        private final String username;
        private final String title;
        private final String createdAt;
        private final int recomCount;
    }

    @Getter
    @AllArgsConstructor
    public static class UserInfoResponse {
        private final Long id;
        private final String title;
    }
}
