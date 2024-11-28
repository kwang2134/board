package com.kwang.board.global.exception.exceptions.comment;

import com.kwang.board.global.exception.BaseException;

public class CommentNotFoundException extends BaseException {
    public CommentNotFoundException(Long comtId) {
        super("Comment not found with id: " + comtId, "C001");
    }
}
