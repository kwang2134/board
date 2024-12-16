package com.kwang.board.post.adapters.mapper;

import com.kwang.board.post.application.dto.PostDTO;
import com.kwang.board.post.application.dto.PostUpdateDTO;
import com.kwang.board.post.domain.model.Post;
import com.kwang.board.post.domain.model.PostType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class PostMapper {
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy:MM:dd");

    public List<PostDTO.UserInfoResponse> toUserInfoResponse(List<Post> posts) {
        return posts.stream()
                .map(post -> new PostDTO.UserInfoResponse(post.getId(), post.getTitle()))
                .collect(Collectors.toList());
    }

    public PostDTO.Response toResponseDTO(Post post) {
        String content = post.getContent();
        content = content.replaceAll("!\\[image\\]\\(([^)]*)\\)", "<img src='$1' alt='Post Image'>");

        return new PostDTO.Response(
                post.getId(),
                post.getDisplayName(),
                post.getTitle(),
                content,
                post.getCreatedAt().format(formatter),
                post.getUpdatedAt().format(formatter),
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
                post.getCreatedAt().format(formatter),
                post.getRecomCount()
                )).toList();
    }

    public PostUpdateDTO toUpdateDTO(PostDTO.Request dto) {
        return new PostUpdateDTO(dto.getTitle(), dto.getContent(), dto.getType());
    }


    public Post toEntity(PostDTO.Request dto) {
        String title = dto.getTitle();
        log.info("[mapper] title = {}", title);

        String content = dto.getContent();
        log.info("[mapper] content = {}", content);

        String displayName = dto.getDisplayName();
        log.info("[mapper] displayName = {}", displayName);

        String password = dto.getPassword();
        log.info("[mapper] password = {}", password);

        log.info("[mapper] dto.getType() = {}", dto.getType());
        PostType postType = PostType.valueOf(dto.getType());
        log.info("[mapper] postType = {}", postType);

        log.info("dto to Entity 변환 성공");

        return Post.builder()
                .title(title)
                .content(content)
                .displayName(displayName)
                .password(password)
                .postType(postType)
                .build();

        /*return Post.builder()
                .title(dto.getTitle())
                .content(dto.getContent())
                .displayName(dto.getDisplayName())
                .password(dto.getPassword())
                .postType(PostType.valueOf(dto.getType()))
                .build();*/
    }
}
