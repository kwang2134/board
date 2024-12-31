package com.kwang.board.user.adapters.mapper;

import com.kwang.board.user.application.dto.user.UserDTO;
import com.kwang.board.user.application.dto.user.UserUpdateDTO;
import com.kwang.board.user.domain.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserMapper {

    private final PasswordEncoder passwordEncoder;

    public UserDTO.Response toDTO(User user) {
        return new UserDTO.Response(
                user.getId(),
                user.getLoginId(),
                user.getUsername(),
                user.getEmail(),
                user.getRole().getValue()
                );
    }

    public UserUpdateDTO toUpdateDTO(UserDTO.Request dto) {
        return new UserUpdateDTO(
                dto.getUsername(),
                dto.getEmail(),
                passwordEncoder.encode(dto.getPassword())
        );
    }

    public User toEntity(UserDTO.Request dto) {
        return User.builder()
                .loginId(dto.getLoginId())
                .username(dto.getUsername())
                .password(passwordEncoder.encode(dto.getPassword()))
                .email(dto.getEmail())
                .build();
    }

    public UserDTO.Request toRequestDTO(User user) {
        return new UserDTO.Request(null, null, user.getPassword(), user.getUsername(), user.getEmail());
    }
}
