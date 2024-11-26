package com.kwang.board.post.application.service;

import com.kwang.board.post.application.dto.PostSearchCond;
import com.kwang.board.post.application.dto.PostUpdateDTO;
import com.kwang.board.post.domain.model.Post;
import com.kwang.board.post.domain.repository.PostRepository;
import com.kwang.board.post.usecase.PostCrudUseCase;
import com.kwang.board.post.usecase.RecommendPostUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService implements PostCrudUseCase, RecommendPostUseCase {

    private final PostRepository repository;

    @Override
    public Post createPost(Post post, List<MultipartFile> photos) {
        return null;
    }

    @Override
    public Post updatePost(Long postId, PostUpdateDTO dto) {
        return null;
    }

    @Override
    public void deletePost(Long postId) {

    }

    @Override
    public Post viewPost(Long postId) {
        return null;
    }

    @Override
    public List<Post> searchPosts(PostSearchCond searchCond) {
        return List.of();
    }

    @Override
    public void recommendPost(Long postId) {

    }

    @Override
    public void notRecommendPost(Long postId) {

    }

    @Override
    public void changeTypeRecom(Long postId) {

    }

    @Override
    public List<Post> viewPopularPosts() {
        return List.of();
    }
}
