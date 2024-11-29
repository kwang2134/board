package com.kwang.board.global.exception.exceptions.post;

import com.kwang.board.global.exception.BaseException;

public class AlreadyRecommendedException extends BaseException {
    public AlreadyRecommendedException(String message) {
        super(message, "P002");
    }
}
