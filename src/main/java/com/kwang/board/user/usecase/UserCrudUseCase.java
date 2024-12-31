package com.kwang.board.user.usecase;

import com.kwang.board.user.application.dto.user.UserUpdateDTO;
import com.kwang.board.user.domain.model.User;

public interface UserCrudUseCase {
    User signupUser(User user);

    User updateUser(Long userId, UserUpdateDTO dto);

    void deleteUser(Long userId);

    User viewUserInfo(Long userId);
}
