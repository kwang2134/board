package com.kwang.board.user.domain.repository;

import com.kwang.board.user.domain.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByLoginId(String loginId);

    Optional<User> findByEmail(String email);

    //유저 정보와 함께 작성한 게시글 fetch join
    @Query("SELECT DISTINCT u FROM User u LEFT JOIN FETCH u.posts p WHERE u.id = :id ORDER BY p.id DESC")
    Optional<User> findUserWithPostsById(Long id);
}
