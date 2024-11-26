package com.kwang.board.comment.application.service;

import com.kwang.board.comment.application.dto.CommentUpdateDTO;
import com.kwang.board.comment.domain.model.Comment;
import com.kwang.board.comment.domain.repository.CommentRepository;
import com.kwang.board.comment.usecase.CommentCrudUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
@RequiredArgsConstructor
public class CommentService implements CommentCrudUseCase {

    private final CommentRepository repository;

    @Override
    public Comment createComt(Comment comt) {
        return null;
    }

    @Override
    public Comment updateComt(Long comtId, CommentUpdateDTO dto) {
        return null;
    }

    @Override
    public void deleteComt(Long comtId) {

    }

    @Override
    public List<Comment> viewComts(Long postId) {
        return List.of();
    }
}
