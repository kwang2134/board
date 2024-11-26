package com.kwang.board.comment.usecase;

import com.kwang.board.comment.application.dto.CommentUpdateDTO;
import com.kwang.board.comment.domain.model.Comment;

import java.util.List;

public interface CommentCrudUseCase {
    Comment createComt(Comment comt);

    Comment updateComt(Long comtId, CommentUpdateDTO dto);

    void deleteComt(Long comtId);

    List<Comment> viewComts(Long postId);

}
