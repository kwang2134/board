package com.kwang.board.comment.adapters.controller;

import com.kwang.board.comment.adapters.mapper.CommentMapper;
import com.kwang.board.comment.application.dto.CommentDTO;
import com.kwang.board.comment.application.service.CommentService;
import com.kwang.board.comment.domain.model.Comment;
import com.kwang.board.global.exception.exceptions.UnauthorizedAccessException;
import com.kwang.board.user.adapters.security.userdetails.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/post/{postId}")
@RequiredArgsConstructor
public class CommentFormController {

    private final CommentService commentService;
    private final CommentMapper commentMapper;

    @GetMapping("/comments/page")
    public String getCommentsByPage(@AuthenticationPrincipal CustomUserDetails userDetails,
                                    @PathVariable("postId") Long postId,
                                    @RequestParam int pageGroup,
                                    @PageableDefault(size = 15) Pageable pageable, Model model) {

        Page<Comment> comments = commentService.viewComts(postId, pageable);

        // 댓글 작성용 객체
        CommentDTO.Request commentRequest = new CommentDTO.Request();
        if (userDetails != null) {
            // 로그인 상태면 작성자명 미리 설정
            commentRequest.setDisplayName(userDetails.getDisplayName());
        }

        int currentPage = comments.getNumber() + 1;
        int totalPages = comments.getTotalPages();
        int startPage = pageGroup * 9 + 1;
        int endPage = Math.min(startPage + 8, totalPages);

        // 댓글 관련 데이터
        model.addAttribute("comments", commentMapper.toResponseListDTO(comments.getContent()));
        model.addAttribute("commentRequest", commentRequest);  // 작성용

        // 페이지네이션 정보
        model.addAttribute("currentPage", currentPage);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("hasNext", comments.hasNext());
        model.addAttribute("hasPrev", comments.hasPrevious());


        return "post/view :: #commentsFragment";
    }

    @GetMapping("/comment/{commentId}/edit")
    public String getCommentEditForm(@AuthenticationPrincipal CustomUserDetails userDetails,
                                     @PathVariable("postId") Long postId,
                                     @PathVariable("commentId") Long commentId,
                                     @RequestParam(required = false) String password,
                                     Model model) {

        Comment comment = commentService.viewComtWithUser(commentId);

        // 회원 댓글인 경우
        if (comment.getUser() != null) {
            if (checkPermission(userDetails, comment)) {
                throw new UnauthorizedAccessException("댓글에 대한 수정 권한이 없습니다.");
            }
        }
        // 비회원 댓글인 경우
        else {
            if (checkPassword(commentId, password)) {
                throw new UnauthorizedAccessException("비밀번호가 일치하지 않습니다.");
            }
        }

        model.addAttribute("commentEditRequest", commentMapper.toRequestDTO(comment));
        return "post/view :: #commentEditForm";
    }

    private boolean checkPassword(Long commentId, String password) {
        return password == null || !commentService.checkNonUserComment(commentId, password);
    }

    private boolean checkPermission(CustomUserDetails userDetails, Comment comment) {
        return userDetails == null || !comment.getUser().getId().equals(userDetails.getId());
    }
}
