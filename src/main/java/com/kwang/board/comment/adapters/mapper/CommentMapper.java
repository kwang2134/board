package com.kwang.board.comment.adapters.mapper;

import com.kwang.board.comment.application.dto.CommentDTO;
import com.kwang.board.comment.application.dto.CommentUpdateDTO;
import com.kwang.board.comment.domain.model.Comment;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
public class CommentMapper {

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yy.MM.dd hh:mm");

    public List<CommentDTO.Response> toResponseListDTO(List<Comment> comments) {
        return comments.stream().map(comment -> new CommentDTO.Response(
                comment.getId(),
                comment.getUser() != null ? comment.getUser().getId() : null,
                comment.getParentComment() != null ? comment.getParentComment().getId() : null,
                comment.getDisplayName(),
                comment.getContent(),
                comment.isDeleted(),
                comment.getCreatedAt().format(formatter),
                comment.getUpdatedAt().format(formatter))
        ).toList();
    }

    public CommentDTO.Request toRequestDTO(Comment comment) {
        return new CommentDTO.Request(
                comment.getId(),
                null,
                comment.getDisplayName(),
                comment.getPassword(),
                comment.getContent()
        );
    }

    public CommentUpdateDTO toUpdateDTO(CommentDTO.Request dto) {
        return new CommentUpdateDTO(dto.getContent());
    }


    public Comment toEntity(CommentDTO.Request dto, Comment parent) {
        return Comment.builder()
                .displayName(dto.getDisplayName())
                .parentComment(parent)
                .password(dto.getPassword())
                .content(dto.getContent())
                .build();
    }

}
