package com.kwang.board.comment.application.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

public class CommentDTO {
    @Data
    public static class UserRequest {
        private Long id;
        private Long postId;
        private Long userId;
        private String content;
        private String createdAt;
        private String updatedAt;
    }

    @Data
    public static class NonUserRequest {
        private Long id;
        private Long postId;
        private String displayName;
        private String password;
        private String content;
        private String createdAt;
        private String updatedAt;
    }

    @Getter
    @AllArgsConstructor
    public static class Response {
        private final Long id;
        private final Long userId;
        private final Long postId;
        private final String username;
        private final String content;
        private final boolean isDeleted;
        private final String createAt;
        private final String updatedAt;
    }

}
