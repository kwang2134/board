package com.kwang.board.post.domain.repository;

import com.kwang.board.post.domain.model.Post;
import com.kwang.board.post.domain.model.PostType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findByUserId(Long userId);

    List<Post> findByPostType(PostType postType);

    Page<Post> findByUserIdOrderByIdDesc(Long userId, Pageable pageable);
}
