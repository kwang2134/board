package com.kwang.board.comment.usecase;

import com.kwang.board.comment.application.dto.CommentUpdateDTO;
import com.kwang.board.comment.domain.model.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CommentCrudUseCase {
    Comment viewComt(Long commentId);

    Comment viewComtWithUser(Long commentId);

    Comment viewComtWithParent(Long commentId);

    Comment viewComtWithUserAndParent(Long commentId);

    Page<Comment> viewComts(Long postId, Pageable pageable);

    List<Comment> viewChildComts(Long parentCommentId);

    Comment createComt(Comment comt, Long postId, Long userId);

    Comment updateComt(Long comtId, CommentUpdateDTO dto);

    void deleteComt(Long comtId);

}
