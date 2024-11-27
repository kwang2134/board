package com.kwang.board.global.exception.exceptions.post;

import com.kwang.board.global.exception.BaseException;

public class PostNotFoundException extends BaseException {

    public PostNotFoundException(Long postId) {
        super("User not found with id: " + postId, "P001");
    }
}
