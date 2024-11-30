package com.kwang.board.user.application.service;

import com.kwang.board.global.exception.exceptions.user.InvalidLoginException;
import com.kwang.board.global.exception.exceptions.user.UserNotFoundException;
import com.kwang.board.user.application.dto.UserUpdateDTO;
import com.kwang.board.user.domain.model.User;
import com.kwang.board.user.domain.repository.UserRepository;
import com.kwang.board.user.usecase.LoginUseCase;
import com.kwang.board.user.usecase.UserCrudUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService implements UserCrudUseCase, LoginUseCase {

    private final UserRepository repository;

    @Override
    @Transactional
    public User signupUser(User user) {
        return repository.save(user);
    }

    @Override
    @Transactional
    public User updateUser(Long userId, UserUpdateDTO dto) {
        User user = repository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
        user.modify(dto);
        return user;
    }

    @Override
    @Transactional
    public void deleteUser(Long userId) {
        repository.deleteById(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public User viewUserInfo(Long userId) {
        return repository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
    }

    @Override
    @Transactional(readOnly = true)
    public User login(String loginId, String password) {
        return repository.findByLoginId(loginId)
                .filter(user -> user.getPassword().equals(password))
                .orElseThrow(InvalidLoginException::new);
    }
}
