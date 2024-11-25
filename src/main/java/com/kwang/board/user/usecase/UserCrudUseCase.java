package com.kwang.board.user.usecase;

import com.kwang.board.user.application.dto.UserDTO;
import com.kwang.board.user.domain.model.User;

public interface UserCrudUseCase {
    User signupUser(UserDTO.Request userDTO);

    User updateUser(Long userId, UserDTO.Request userDTO);

    void deleteUser(Long userId);

    UserDTO.Response viewUserInfo(Long userId);
}
