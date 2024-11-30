package com.kwang.board.user.application.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserUpdateDTO {

    private String username;
    private String email;
    private String password;
}
