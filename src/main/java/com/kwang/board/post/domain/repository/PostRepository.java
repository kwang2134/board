package com.kwang.board.post.domain.repository;

import com.kwang.board.post.domain.model.Post;
import com.kwang.board.post.domain.model.PostType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findByUserId(Long userId);

    Page<Post> findByPostType(PostType postType, Pageable pageable);

    Page<Post> findByUserIdOrderByIdDesc(Long userId, Pageable pageable);

    Optional<Post> findByUserIdAndId(Long userId, Long postId);
}
