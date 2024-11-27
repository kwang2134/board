package com.kwang.board.post.usecase;

import com.kwang.board.post.application.dto.PostSearchCond;
import com.kwang.board.post.application.dto.PostUpdateDTO;
import com.kwang.board.post.domain.model.Post;

import java.util.List;

public interface PostCrudUseCase {
    Post createPost(Post post);

    Post updatePost(Long postId, PostUpdateDTO dto);

    void deletePost(Long postId);

    Post viewPost(Long postId);

    List<Post> searchPosts(PostSearchCond searchCond);

    void changeToPopular(Long postId);

    void changeToNormal(Long postId);

    void changeToNotice(Long postId);
}
