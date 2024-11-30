package com.kwang.board.comment.adapters.mapper;

import com.kwang.board.comment.application.dto.CommentDTO;
import com.kwang.board.comment.application.dto.CommentUpdateDTO;
import com.kwang.board.comment.domain.model.Comment;
import org.springframework.stereotype.Component;

@Component
public class CommentMapper {

    public CommentDTO.Response toDTO(Comment comment, Long userId, Long postId, String displayName) {
        return new CommentDTO.Response(
                comment.getId(),
                userId,
                postId,
                displayName,
                comment.getContent(),
                comment.isDeleted(),
                comment.getCreatedAt().toString(),
                comment.getUpdatedAt().toString());
    }

    public CommentUpdateDTO toUpdateDTO(CommentDTO.UserRequest dto) {
        return new CommentUpdateDTO(dto.getContent());
    }

    public CommentUpdateDTO toUpdateDTO(CommentDTO.NonUserRequest dto) {
        return new CommentUpdateDTO(dto.getContent());
    }

    public Comment toEntity(CommentDTO.UserRequest dto) {
        return Comment.builder()
                .content(dto.getContent())
                .build();
    }

    public Comment toEntity(CommentDTO.NonUserRequest dto) {
        return Comment.builder()
                .displayName(dto.getDisplayName())
                .password(dto.getPassword())
                .content(dto.getContent())
                .build();
    }

}
