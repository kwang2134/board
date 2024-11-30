package com.kwang.board.post.application.service;

import com.kwang.board.global.exception.exceptions.post.AlreadyNotRecommendedException;
import com.kwang.board.global.exception.exceptions.post.AlreadyRecommendedException;
import com.kwang.board.global.exception.exceptions.post.PostNotFoundException;
import com.kwang.board.post.application.dto.PostSearchCond;
import com.kwang.board.post.application.dto.PostUpdateDTO;
import com.kwang.board.post.domain.model.Post;
import com.kwang.board.post.domain.model.PostType;
import com.kwang.board.post.domain.repository.PostQueryRepository;
import com.kwang.board.post.domain.repository.PostRepository;
import com.kwang.board.post.usecase.PostCrudUseCase;
import com.kwang.board.post.usecase.RecommendPostUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostService implements PostCrudUseCase, RecommendPostUseCase {

    private final PostRepository repository;
    private final PostQueryRepository queryRepository;

    private final RedisTemplate<String, String> redisTemplate;
    private static final long RECOMMEND_EXPIRE_TIME = 24 * 60 * 60; // 24시간

    @Override
    @Transactional
    public Post createPost(Post post) {
        return repository.save(post);
    }

    @Override
    @Transactional
    public Post updatePost(Long postId, PostUpdateDTO dto) {
        Post post = repository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException(postId));
        post.modify(dto);
        return post;
    }

    @Override
    @Transactional
    public void deletePost(Long postId) {
        repository.deleteById(postId);
    }

    @Override
    @Transactional(readOnly = true)
    public Post viewPost(Long postId) {
        return repository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException(postId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Post> searchPosts(PostSearchCond searchCond) {
        return queryRepository.searchPosts(searchCond);
    }

    @Override
    @Transactional
    public void recommendPost(Long postId, Long userId, String sessionId) {
        String recommendKey = userId != null ?
                getRecommendKey(postId, "user" + userId) :
                getRecommendKey(postId, sessionId);

        String notRecommendKey = userId != null ?
                getNotRecommendKey(postId, "user" + userId) :
                getNotRecommendKey(postId, sessionId);

        // 이미 추천했는지 확인
        if (Boolean.TRUE.equals(redisTemplate.hasKey(recommendKey))) {
            throw new AlreadyRecommendedException("이미 추천한 게시글입니다.");
        }

        // 비추천 여부 확인
        if (Boolean.TRUE.equals(redisTemplate.hasKey(notRecommendKey))) {
            throw new AlreadyNotRecommendedException("이미 비추천한 게시글입니다.");
        }

        Post post = repository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException(postId));

        post.recommendPost();

        // Redis에 추천 이력 저장
        redisTemplate.opsForValue().set(recommendKey, "true", RECOMMEND_EXPIRE_TIME, TimeUnit.SECONDS);
    }

    @Override
    @Transactional
    public void notRecommendPost(Long postId, Long userId, String sessionId) {
        String recommendKey = userId != null ?
                getRecommendKey(postId, String.valueOf(userId)) :
                getRecommendKey(postId, sessionId);

        String notRecommendKey = userId != null ?
                getNotRecommendKey(postId, String.valueOf(userId)) :
                getNotRecommendKey(postId, sessionId);

        // 이미 추천했는지 확인
        if (Boolean.TRUE.equals(redisTemplate.hasKey(recommendKey))) {
            throw new AlreadyRecommendedException("이미 추천한 게시글입니다.");
        }


        if (Boolean.TRUE.equals(redisTemplate.hasKey(notRecommendKey))) {
            throw new AlreadyNotRecommendedException("이미 비추천한 게시글입니다.");
        }

        Post post = repository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException(postId));

        post.notRecommendPost();
        redisTemplate.opsForValue().set(notRecommendKey, "true", RECOMMEND_EXPIRE_TIME, TimeUnit.SECONDS);
    }

    private String getRecommendKey(Long postId, String id) {
        return String.format("recommend:%s:%d", id, postId);
    }

    private String getNotRecommendKey(Long postId, String id) {
        return String.format("notRecommend:%s:%d", id, postId);
    }

    @Override
    @Transactional
    public void changeToPopular(Long postId) {
        Post post = repository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException(postId));

        post.changeTypePopular();
    }

    @Override
    @Transactional
    public void changeToNormal(Long postId) {
        Post post = repository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException(postId));

        post.changeTypeNormal();
    }

    @Override
    @Transactional
    public void changeToNotice(Long postId) {
        Post post = repository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException(postId));

        post.changeTypeNotice();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Post> viewUserPosts(Long userId, Pageable pageable) {
        return repository.findByUserIdOrderByIdDesc(userId, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Post> viewPopularPosts() {
        return repository.findByPostType(PostType.POPULAR);
    }
}
