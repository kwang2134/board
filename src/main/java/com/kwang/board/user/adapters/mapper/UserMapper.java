package com.kwang.board.user.adapters.mapper;

import com.kwang.board.post.application.dto.PostDTO;
import com.kwang.board.user.application.dto.UserDTO;
import com.kwang.board.user.application.dto.UserUpdateDTO;
import com.kwang.board.user.domain.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class UserMapper {

    private final PasswordEncoder passwordEncoder;

    public UserDTO.Response toDTO(User user, List<PostDTO.UserInfoResponse> posts) {
        return new UserDTO.Response(
                user.getId(),
                user.getLoginId(),
                user.getUsername(),
                user.getEmail(),
                user.getRole().getValue(),
                posts
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
}
