package com.kwang.board.global.exception;


import lombok.Getter;

public class BaseException extends RuntimeException{
    private final String message;
    @Getter
    private final String code;    // 예외 코드

    protected BaseException(String message, String code) {
        super(message);
        this.message = message;
        this.code = code;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
