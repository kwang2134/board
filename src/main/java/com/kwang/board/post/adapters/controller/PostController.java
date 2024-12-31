package com.kwang.board.post.adapters.controller;

import com.kwang.board.global.aop.annotation.SavePostRequest;
import com.kwang.board.global.exception.exceptions.UnauthorizedAccessException;
import com.kwang.board.photo.application.service.PhotoService;
import com.kwang.board.post.adapters.mapper.PostMapper;
import com.kwang.board.post.application.dto.PostDTO;
import com.kwang.board.post.application.service.PostService;
import com.kwang.board.post.domain.model.Post;
import com.kwang.board.user.adapters.security.userdetails.CustomUserDetails;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Controller
@RequestMapping("/post")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;
    private final PhotoService photoService;

    private final PostMapper postMapper;

    /**
     * 작성된 게시글 내용 session 에 임시 저장
     * 성공 시 session 에 임시 저장된 내용 삭제
     * 실패 시 다시 작성 폼으로 redirect
     */
    @PostMapping("/write")
    @SavePostRequest
    public String createPost(@AuthenticationPrincipal CustomUserDetails userDetails,
                             @Valid @ModelAttribute PostDTO.Request request,
                             HttpSession session) {

        Long userId = userDetails != null ? userDetails.getId() : null;
        Post createdPost = postService.createPost(postMapper.toEntity(request), userId);

        photoService.uploadPhoto(createdPost.getId(), session.getId());
        return "redirect:/post/" + createdPost.getId();
    }

    /**
     * 수정된 게시글 내용 session 에 임시 저장
     * 성공 시 session 에 임시 저장된 내용 삭제
     * 실패 시 다시 수정 폼으로 redirect
     */
    @PostMapping("/{id}/edit")
    @SavePostRequest
    public String updatePost(@Valid @ModelAttribute PostDTO.Request request,
                             @PathVariable("id") Long postId, HttpSession session) {

        postService.updatePost(postId, postMapper.toUpdateDTO(request));
        photoService.updatePhoto(postId, session.getId());

        return "redirect:/post/" + postId;
    }

    @PostMapping("/{id}/delete")
    public String deletePost(@AuthenticationPrincipal CustomUserDetails userDetails,
                             @PathVariable("id") Long postId,
                             @RequestParam(required = false) String password) {
        log.info("게시글 삭제 호출");
        Post post = postService.viewPost(postId);

        // 회원 게시글인 경우
        if (post.getUser() != null) {
            if (checkPermission(userDetails, post)) {
                throw new UnauthorizedAccessException("게시글에 대한 삭제 권한이 없습니다.");
            }
        }

        // 비회원 게시글인 경우
        else {
            if (checkPassword(postId, password)) {
                throw new UnauthorizedAccessException("비밀번호가 일치하지 않습니다.");
            }
        }

        log.info("게시글 삭제 성공");
        photoService.deletePhoto(postId);
        postService.deletePost(postId);
        return "redirect:/";
    }

    @PostMapping("/post/write/cancel")
    public String cancelCreatePost(HttpSession session) {
        session.removeAttribute("postCreateRequest");
        return "redirect:/";
    }

    @PostMapping("/post/{id}/edit/cancel")
    public String cancelEditPost(HttpSession session) {
        session.removeAttribute("postEditRequest");
        return "redirect:/";
    }

    private boolean checkPassword(Long postId, String password) {
        return password == null || !postService.checkNonUserPost(postId, password);
    }

    private boolean checkPermission(CustomUserDetails userDetails, Post post) {
        return userDetails == null || !post.getUser().getId().equals(userDetails.getId());
    }
}
