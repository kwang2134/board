package com.kwang.board.user.application.dto;

import lombok.Data;

@Data
public class UserUpdateDTO {

    private String username;
    private String email;
    private String password;
}
