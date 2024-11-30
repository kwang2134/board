package com.kwang.board.post.adapters.mapper;

import com.kwang.board.comment.application.dto.CommentDTO;
import com.kwang.board.photo.application.dto.PhotoDTO;
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

    public PostDTO.Response toDTO(Post post, List<CommentDTO.Response> comments, List<PhotoDTO> photos) {
        return new PostDTO.Response(
                post.getId(),
                post.getDisplayName(),
                post.getTitle(),
                post.getContent(),
                post.getCreatedAt().toString(),
                post.getUpdatedAt().toString(),
                post.getRecomCount(),
                post.getNotRecomCount(),
                        comments,
                        photos
//                post.getComments(),
//                post.getPhotos()
        );
    }

    public PostUpdateDTO toUpdateDTO(PostDTO.UserRequest dto) {
        return new PostUpdateDTO(dto.getTitle(), dto.getContent(), dto.getType());
    }

    public PostUpdateDTO toUpdateDTO(PostDTO.NonUserRequest dto) {
        return new PostUpdateDTO(dto.getTitle(), dto.getContent(), dto.getType());
    }

    public Post toEntity(PostDTO.UserRequest dto) {
        return Post.builder()
                .title(dto.getTitle())
                .content(dto.getContent())
                .postType(PostType.valueOf(dto.getType()))
                .recomCount(dto.getRecomCount())
                .notRecomCount(dto.getNotRecomCount())
                .build();
    }

    public Post toEntity(PostDTO.NonUserRequest dto) {
        return Post.builder()
                .displayName(dto.getDisplayName())
                .password(dto.getPassword())
                .title(dto.getTitle())
                .content(dto.getContent())
                .postType(PostType.valueOf(dto.getType()))
                .recomCount(dto.getRecomCount())
                .notRecomCount(dto.getNotrecomCount())
                .build();
    }
}
