package com.kwang.board.comment.domain.repository;

import com.kwang.board.comment.domain.model.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    Page<Comment> findByPostId(Long postId, Pageable pageable);

    List<Comment> findByParentCommentId(Long parentCommentId);

    @Query("SELECT c FROM Comment c JOIN FETCH c.user WHERE c.id = :commentId")
    Optional<Comment> findByIdWithUser(@Param("commentId") Long commentId);

    @Query("SELECT c FROM Comment c JOIN FETCH c.parentComment WHERE c.id = :commentId")
    Optional<Comment> findByIdWithParent(@Param("commentId") Long commentId);

    @Query("SELECT c FROM Comment c " +
            "LEFT JOIN FETCH c.user " +
            "LEFT JOIN FETCH c.parentComment " +
            "WHERE c.id = :commentId")
    Optional<Comment> findByIdWithUserAndParent(@Param("commentId") Long commentId);


}
