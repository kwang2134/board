package com.kwang.board.user.domain.repository;

import com.kwang.board.user.domain.model.Role;
import com.kwang.board.user.domain.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByLoginId(String loginId);

    Optional<User> findByEmail(String email);

    List<User> findUsersByRole(Role role);
}
