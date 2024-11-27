package com.kwang.board.post.usecase;

import com.kwang.board.post.domain.model.Post;

import java.util.List;

public interface RecommendPostUseCase {

    void recommendPost(Long postId);

    void notRecommendPost(Long postId);

    List<Post> viewPopularPosts();
}
