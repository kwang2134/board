package com.kwang.board.post.adapters.controller;

import com.kwang.board.global.exception.exceptions.post.AlreadyNotRecommendedException;
import com.kwang.board.global.exception.exceptions.post.AlreadyRecommendedException;
import com.kwang.board.post.application.service.PostService;
import com.kwang.board.post.domain.model.Post;
import com.kwang.board.user.adapters.security.userdetails.CustomUserDetails;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/post")
@RequiredArgsConstructor
public class PostApiController {
    private final PostService postService;

    @PostMapping("/{id}/recommend")
    public ResponseEntity<?> recommendPost(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable("id") Long postId,
            HttpSession session) {
        try {
            Long userId = userDetails != null ? userDetails.getId() : null;
            postService.recommendPost(postId, userId, session.getId());
            Post post = postService.viewPost(postId);

            return ResponseEntity.ok(Map.of(
                    "message", "추천이 완료되었습니다.",
                    "recomCount", post.getRecomCount()
            ));
        } catch (AlreadyRecommendedException | AlreadyNotRecommendedException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "message", e.getMessage()
            ));
        }
    }

    @PostMapping("/{id}/not-recommend")
    public ResponseEntity<?> notRecommendPost(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable("id") Long postId,
            HttpSession session) {
        try {
            Long userId = userDetails != null ? userDetails.getId() : null;
            postService.notRecommendPost(postId, userId, session.getId());
            Post post = postService.viewPost(postId);

            return ResponseEntity.ok(Map.of(
                    "message", "비추천이 완료되었습니다.",
                    "notRecomCount", post.getNotRecomCount()
            ));
        } catch (AlreadyRecommendedException | AlreadyNotRecommendedException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "message", e.getMessage()
            ));
        }
    }
}
