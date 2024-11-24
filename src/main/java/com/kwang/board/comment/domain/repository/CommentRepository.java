package com.kwang.board.comment.domain.repository;

import com.kwang.board.comment.domain.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByPostId(Long postId);
    List<Comment> findByParentCommentId(Long parentCommentId);
}
