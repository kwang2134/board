package com.kwang.board.post.application.service;

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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostService implements PostCrudUseCase, RecommendPostUseCase {

    private final PostRepository repository;
    private final PostQueryRepository queryRepository;

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
    public void recommendPost(Long postId) {
        Post post = repository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException(postId));

        int recommendCount = post.recommendPost();
    }

    @Override
    @Transactional
    public void notRecommendPost(Long postId) {
        Post post = repository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException(postId));

        int notRecommendCount = post.notRecommendPost();
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
    public List<Post> viewPopularPosts() {
        return repository.findByPostType(PostType.POPULAR);
    }
}
