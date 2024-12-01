package com.kwang.board.post.domain.repository;

import com.kwang.board.post.application.dto.PostSearchCond;
import com.kwang.board.post.domain.model.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PostQueryRepository {
    Page<Post> searchPosts(PostSearchCond searchCond, Pageable pageable);
}
