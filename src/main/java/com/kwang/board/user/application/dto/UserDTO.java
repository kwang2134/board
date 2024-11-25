package com.kwang.board.user.application.dto;

import com.kwang.board.post.application.dto.PostDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

import java.util.List;

public class UserDTO {

    @Data
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
        private final List<PostDTO.UserInfoResponse> posts;
    }

}
