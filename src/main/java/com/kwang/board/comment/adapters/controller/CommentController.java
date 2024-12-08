package com.kwang.board.comment.adapters.controller;

import com.kwang.board.comment.adapters.mapper.CommentMapper;
import com.kwang.board.comment.application.dto.CommentDTO;
import com.kwang.board.comment.application.service.CommentService;
import com.kwang.board.comment.domain.model.Comment;
import com.kwang.board.global.exception.exceptions.UnauthorizedAccessException;
import com.kwang.board.user.adapters.security.userdetails.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/manage/post/{postId}/comment")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    private final CommentMapper commentMapper;


    @PostMapping("/write")
    public String createComment(@AuthenticationPrincipal CustomUserDetails userDetails,
                                @Valid @ModelAttribute CommentDTO.Request commentRequest,
                                @PathVariable("postId") Long postId) {

        Comment parentComment = commentRequest.getParentId() != null ?
                commentService.viewComt(commentRequest.getParentId()) : null;

        Long userId = userDetails != null ? userDetails.getId() : null;

        commentService.createComt(
                commentMapper.toEntity(commentRequest, parentComment),
                postId,
                userId);


        return "redirect:/post/" + postId;
    }

    @PostMapping("/{commentId}/edit")
    public String updateComment(@Valid @ModelAttribute CommentDTO.Request commentEditRequest,
                                @PathVariable("commentId") Long commentId,
                                @PathVariable("postId") Long postId) {
        commentService.updateComt(commentId, commentMapper.toUpdateDTO(commentEditRequest));
        return "redirect:/post/" + postId;
    }

    @PostMapping("/{commentId}/delete")
    public String deleteComment(@AuthenticationPrincipal CustomUserDetails userDetails,
                                @PathVariable("commentId") Long commentId,
                                @PathVariable("postId") Long postId,
                                @RequestParam(required = false) String password){

        Comment comment = commentService.viewComtWithUser(commentId);

        // 회원 댓글인 경우
        if (comment.getUser() != null) {
            if (checkPermission(userDetails, comment)) {
                throw new UnauthorizedAccessException("댓글에 대한 삭제 권한이 없습니다.");
            }
        }

        // 비회원 댓글인 경우
        else {
            if (checkPassword(commentId, password)) {
                throw new UnauthorizedAccessException("비밀번호가 일치하지 않습니다.");
            }
        }

        commentService.deleteComt(commentId);

        return "redirect:/post/" + postId;
    }

    private boolean checkPassword(Long commentId, String password) {
        return password == null || !commentService.checkNonUserComment(commentId, password);
    }

    private boolean checkPermission(CustomUserDetails userDetails, Comment comment) {
        return userDetails == null || !comment.getUser().getId().equals(userDetails.getId());
    }
}
