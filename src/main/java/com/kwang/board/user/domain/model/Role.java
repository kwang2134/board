package com.kwang.board.user.domain.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Role {
    USER("USER"),
    MANAGER("MANAGER"),
    ADMIN("ADMIN");

    private final String value;
}
