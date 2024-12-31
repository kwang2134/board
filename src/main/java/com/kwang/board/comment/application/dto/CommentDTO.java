package com.kwang.board.comment.application.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class CommentDTO {

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Request {
        private Long id;
        private Long parentId;
        private String displayName;
        private String password;
        private String content;
    }

    @Getter
    @AllArgsConstructor
    public static class Response {
        private final Long id;
        private final Long userId;
        private final Long parentId;
        private final String username;
        private final String content;
        private final boolean isDeleted;
        private final String createdAt;
        private final String updatedAt;
        private final int depth;
    }

}
