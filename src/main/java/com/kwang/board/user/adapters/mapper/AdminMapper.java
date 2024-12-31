package com.kwang.board.user.adapters.mapper;

import com.kwang.board.user.application.dto.admin.AdminDTO;
import com.kwang.board.user.domain.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class AdminMapper {

    public List<AdminDTO> toUserList(List<User> users) {
        return users.stream().map(user -> new AdminDTO(
                user.getId(),
                user.getLoginId(),
                user.getPassword(),
                user.getUsername(),
                user.getEmail(),
                user.getRole().toString())).toList();
    }
}
