package com.kwang.board.global.exception.exceptions.post;

import com.kwang.board.global.exception.BaseException;

public class AlreadyNotRecommendedException extends BaseException {
    public AlreadyNotRecommendedException(String message) {
        super(message, "P003");
    }
}
