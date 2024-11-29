package com.kwang.board.post.usecase;

import com.kwang.board.post.domain.model.Post;

import java.util.List;

public interface RecommendPostUseCase {

    void recommendPost(Long postId, Long userId, String sessionId);

    void notRecommendPost(Long postId, Long userId, String sessionId);

    List<Post> viewPopularPosts();
}
