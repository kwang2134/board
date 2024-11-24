package com.kwang.board.post.domain.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PostType {
    NORMAL("NORMAL"),
    NOTICE("NOTICE"),
    POPULAR("POPULAR");

    private final String value;
}
