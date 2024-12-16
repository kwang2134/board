package com.kwang.board.user.usecase;

import com.kwang.board.user.domain.model.User;

public interface LoginUseCase {
    User login(String loginId, String password);

    boolean isLoginIdAvailable(String loginId);
}
