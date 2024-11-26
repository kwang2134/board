package com.kwang.board.global.exception.exceptions.user;

import com.kwang.board.global.exception.BaseException;

public class InvalidLoginException extends BaseException {
    public InvalidLoginException() {
        super("Invalid login credentials", "U002");
    }
}
