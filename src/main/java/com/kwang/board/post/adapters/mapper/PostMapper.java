package com.kwang.board.post.adapters.mapper;

import com.kwang.board.post.application.dto.PostDTO;
import com.kwang.board.post.application.dto.PostUpdateDTO;
import com.kwang.board.post.domain.model.Post;
import com.kwang.board.post.domain.model.PostType;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class PostMapper {
    public List<PostDTO.UserInfoResponse> toUserInfoResponse(List<Post> posts) {
        return posts.stream()
                .map(post -> new PostDTO.UserInfoResponse(post.getId(), post.getTitle()))
                .collect(Collectors.toList());
    }

    public PostDTO.Response toResponseDTO(Post post) {
        return new PostDTO.Response(
                post.getId(),
                post.getDisplayName(),
                post.getTitle(),
                post.getContent(),
                post.getCreatedAt().toString(),
                post.getUpdatedAt().toString(),
                post.getRecomCount(),
                post.getNotRecomCount()
        );
    }

    public PostDTO.Request toRequestDTO(Post post) {
        return new PostDTO.Request(post.getTitle(),
                post.getContent(),
                post.getPostType().toString(),
                null,
                null);
    }

    public List<PostDTO.ListResponse> toDTOList(List<Post> posts) {
        return posts.stream().map(post -> new PostDTO.ListResponse(
                post.getId(),
                post.getDisplayName(),
                post.getTitle(),
                post.getCreatedAt().toString(),
                post.getRecomCount()
                )).toList();
    }

    public PostUpdateDTO toUpdateDTO(PostDTO.Request dto) {
        return new PostUpdateDTO(dto.getTitle(), dto.getContent(), dto.getType());
    }


    public Post toEntity(PostDTO.Request dto) {
        return Post.builder()
                .title(dto.getTitle())
                .content(dto.getContent())
                .displayName(dto.getDisplayName())
                .password(dto.getPassword())
                .postType(PostType.valueOf(dto.getType()))
                .build();
    }
}
