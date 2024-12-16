package com.kwang.board.post.domain.repository;

import com.kwang.board.post.domain.model.Post;
import com.kwang.board.post.domain.model.PostType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findByUserId(Long userId);

    @Query("SELECT p FROM Post p LEFT JOIN FETCH p.user WHERE p.id = :postId")
    Optional<Post> findByIdWithUser(@Param("postId") Long postId);

    Page<Post> findByPostTypeOrderByIdDesc(PostType postType, Pageable pageable);

    Page<Post> findByUserIdOrderByIdDesc(Long userId, Pageable pageable);

    Optional<Post> findByUserIdAndId(Long userId, Long postId);
}
