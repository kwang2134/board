package com.kwang.board.comment.adapters.controller;

import com.kwang.board.comment.adapters.mapper.CommentMapper;
import com.kwang.board.comment.application.dto.CommentDTO;
import com.kwang.board.comment.application.service.CommentService;
import com.kwang.board.comment.domain.model.Comment;
import com.kwang.board.global.exception.exceptions.UnauthorizedAccessException;
import com.kwang.board.user.adapters.security.userdetails.CustomUserDetails;
import com.kwang.board.user.domain.model.Role;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Controller
@RequestMapping("/post/{postId}/comment")
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

        if (parentComment == null) {
            log.info("원본 댓글 작성");
        } else {
            log.info("대댓글 작성, 원본 댓글 아이디 ={}", commentRequest.getParentId());
        }

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

        log.info("댓글 삭제 호출");
        Comment comment = commentService.viewComtWithUser(commentId);

        // 회원 댓글인 경우
        if (comment.getUser() != null) {
            if (checkPermission(userDetails, comment)) {
                log.info("CommentController 삭제 권한 없음(회원)");
                throw new UnauthorizedAccessException("댓글에 대한 삭제 권한이 없습니다.");
            }
        }

        // 비회원 댓글인 경우
        else {
            if (checkPassword(commentId, password)) {
                log.info("CommentController 삭제 권한 없음(비회원)");
                throw new UnauthorizedAccessException("비밀번호가 일치하지 않습니다.");
            }
        }

        log.info("CommentController 댓글 삭제 권한 통과");
        commentService.deleteComt(commentId);

        return "redirect:/post/" + postId;
    }

    private boolean checkPassword(Long commentId, String password) {
        log.info("commentId = {}, password = {}", commentId, password);
        return password == null || !commentService.checkNonUserComment(commentId, password);
    }

    private boolean checkPermission(CustomUserDetails userDetails, Comment comment) {
        // 사용자가 없거나 댓글 작성자가 아닌 경우
        if (userDetails == null || !comment.getUser().getId().equals(userDetails.getId())) {
            // 매니저 이상 권한인 경우 삭제 허용
            return userDetails == null ||
                    !(userDetails.getRole() == Role.MANAGER || userDetails.getRole() == Role.ADMIN);
        }
        return false;  // 댓글 작성자인 경우 삭제 허용
    }
}
