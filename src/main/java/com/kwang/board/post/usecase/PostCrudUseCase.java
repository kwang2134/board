package com.kwang.board.post.usecase;

import com.kwang.board.post.application.dto.PostSearchCond;
import com.kwang.board.post.application.dto.PostUpdateDTO;
import com.kwang.board.post.domain.model.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PostCrudUseCase {
    Post createPost(Post post, Long userId);

    Post updatePost(Long postId, PostUpdateDTO dto);

    void deletePost(Long postId);

    Post viewPost(Long postId);

    Page<Post> searchPosts(PostSearchCond searchCond, Pageable pageable);

    void changeToPopular(Long postId);

    void changeToNormal(Long postId);

    void changeToNotice(Long postId);

    Page<Post> viewUserPosts(Long userId, Pageable pageable);

    Page<Post> viewNormalPosts(Pageable pageable);

    Page<Post> viewNoticePosts(Pageable pageable);

    Page<Post> viewPopularPosts(Pageable pageable);

    boolean checkNonUserPost(Long postId, String password);
}
