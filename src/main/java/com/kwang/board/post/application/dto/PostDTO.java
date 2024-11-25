package com.kwang.board.post.application.dto;

import com.kwang.board.comment.application.dto.CommentDTO;
import com.kwang.board.photo.application.dto.PhotoDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public class PostDTO {

    @Data
    public static class UserRequest {

        private Long id;
        private Long userId;
        private String title;
        private String content;
        private String type;
        private int recomCount;
        private int notRecomCount;
        private List<MultipartFile> photos;
        private String createAt;
        private String updatedAt;

    }

    @Data
    public static class NonUserRequest {

        private Long id;
        private String displayName;
        private String password;
        private String title;
        private String content;
        private String type;
        private int recomCount;
        private int notrecomCount;
        private List<MultipartFile> photos;
        private String createAt;
        private String updatedAt;

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
        private final List<CommentDTO.Response> comments;
        private final List<PhotoDTO> photos;
    }

    @Getter
    @AllArgsConstructor
    public static class UserInfoResponse {
        private final Long id;
        private final String title;
    }
}
