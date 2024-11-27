package com.kwang.board.post.domain.repository;

import com.kwang.board.post.application.dto.PostSearchCond;
import com.kwang.board.post.domain.model.Post;

import java.util.List;

public interface PostQueryRepository {
    List<Post> searchPosts(PostSearchCond searchCond);
}
