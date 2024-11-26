package com.kwang.board.global.exception.exceptions.user;

import com.kwang.board.global.exception.BaseException;

public class UserNotFoundException extends BaseException {

    public UserNotFoundException(Long userId) {
        super("User not found with id: " + userId, "U001");
    }
}
