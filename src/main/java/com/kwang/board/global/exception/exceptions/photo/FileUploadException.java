package com.kwang.board.global.exception.exceptions.photo;

import com.kwang.board.global.exception.BaseException;

public class FileUploadException extends BaseException {

    public FileUploadException(String message) {
        super(message, "F001");
    }
}
