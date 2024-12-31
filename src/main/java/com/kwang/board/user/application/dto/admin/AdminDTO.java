package com.kwang.board.user.application.dto.admin;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AdminDTO {

    private final Long id;
    private final String loginId;
    private final String password;
    private final String username;
    private final String email;
    private final String role;
}
