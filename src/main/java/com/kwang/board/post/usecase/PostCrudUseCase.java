package com.kwang.board.post.usecase;

import com.kwang.board.post.application.dto.PostSearchCond;
import com.kwang.board.post.application.dto.PostUpdateDTO;
import com.kwang.board.post.domain.model.Post;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface PostCrudUseCase {
    Post createPost(Post post, List<MultipartFile> photos);

    Post updatePost(Long postId, PostUpdateDTO dto);

    void deletePost(Long postId);

    Post viewPost(Long postId);

    List<Post> searchPosts(PostSearchCond searchCond);
}
