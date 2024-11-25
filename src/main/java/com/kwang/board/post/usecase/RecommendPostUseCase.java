package com.kwang.board.post.usecase;

import com.kwang.board.post.application.dto.PostDTO;

import java.util.List;

public interface RecommendPostUseCase {
    void recommendPost(Long postId);

    void notRecommendPost(Long postId);

    void changeTypeRecom(Long postId);

    List<PostDTO.Response> viewPopularPosts();
}
