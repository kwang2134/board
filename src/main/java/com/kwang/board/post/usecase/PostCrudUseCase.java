package com.kwang.board.post.usecase;

import com.kwang.board.post.application.dto.PostDTO;
import com.kwang.board.post.application.dto.PostSearchCond;
import com.kwang.board.post.domain.model.Post;

import java.util.List;

public interface PostCrudUseCase {
    Post createPost(PostDTO.UserRequest postDTO);

    Post createPost(PostDTO.NonUserRequest postDTO);

    Post updatePost(Long postId, PostDTO.UserRequest postDTO);

    Post updatePost(Long postId, PostDTO.NonUserRequest postDTO);

    void deletePost(Long postId);

    PostDTO.Response viewPost(Long postId);

    List<PostDTO.Response> searchPosts(PostSearchCond searchCond);
}
