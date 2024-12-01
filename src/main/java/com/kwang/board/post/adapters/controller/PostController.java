package com.kwang.board.post.adapters.controller;

import com.kwang.board.photo.application.service.PhotoService;
import com.kwang.board.post.adapters.mapper.PostMapper;
import com.kwang.board.post.application.dto.PostDTO;
import com.kwang.board.post.application.service.PostService;
import com.kwang.board.post.domain.model.Post;
import com.kwang.board.user.adapters.security.userdetails.CustomUserDetails;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/manage/post")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;
    private final PhotoService photoService;

    private final PostMapper postMapper;

    @PostMapping("/write")
    public String createPost(@AuthenticationPrincipal CustomUserDetails userDetails,
                             @Valid @ModelAttribute PostDTO.Request request,
                             HttpSession session) {

        Post createdPost;

        if (userDetails != null) {
            // 회원 게시글
            createdPost = postService.createPost(postMapper.toEntity(request), userDetails.getId());
        } else {
            // 비회원 게시글
            createdPost= postService.createPost(postMapper.toEntity(request), null);
        }

        photoService.uploadPhoto(createdPost.getId(), session.getId());
        return "redirect:/";
    }

    @PostMapping("/{id}/edit")
    public String updatePost(@Valid @ModelAttribute PostDTO.Request request,
                             @PathVariable("id") Long postId, HttpSession session) {

        postService.updatePost(postId, postMapper.toUpdateDTO(request));
        photoService.updatePhoto(postId, session.getId());

        return "redirect:/post/" + postId;
    }
}
