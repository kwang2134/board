package com.kwang.board.post.usecase;

public interface RecommendPostUseCase {

    void recommendPost(Long postId, Long userId, String sessionId);

    void notRecommendPost(Long postId, Long userId, String sessionId);
}
