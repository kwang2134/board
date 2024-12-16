package com.kwang.board.comment.application.service;

import com.kwang.board.comment.application.dto.CommentUpdateDTO;
import com.kwang.board.comment.domain.model.Comment;
import com.kwang.board.comment.domain.repository.CommentRepository;
import com.kwang.board.comment.usecase.CommentCrudUseCase;
import com.kwang.board.global.exception.exceptions.comment.CommentNotFoundException;
import com.kwang.board.global.exception.exceptions.post.PostNotFoundException;
import com.kwang.board.global.exception.exceptions.user.UserNotFoundException;
import com.kwang.board.post.domain.model.Post;
import com.kwang.board.post.domain.repository.PostRepository;
import com.kwang.board.user.domain.model.User;
import com.kwang.board.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentService implements CommentCrudUseCase {

    private final CommentRepository repository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public Comment createComt(Comment comt, Long postId, Long userId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException(postId));
        comt.connectPost(post);

        if (userId != null) {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new UserNotFoundException(userId));
            comt.connectUser(user);
        }

        return repository.save(comt);
    }

    @Override
    @Transactional
    public Comment updateComt(Long comtId, CommentUpdateDTO dto) {
         Comment comt = repository.findById(comtId)
                .orElseThrow(() -> new CommentNotFoundException(comtId));

         comt.modify(dto);
         return comt;
    }

    @Override
    @Transactional
    public void deleteComt(Long commentId) {
        Comment comment = repository.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException(commentId));

        List<Comment> childComments = repository.findByParentCommentId(commentId);

        if (!childComments.isEmpty()) {
            // 자식 댓글이 있는 경우 상태만 변경
            comment.changeStateToDelete();
            // 자식 댓글들의 상태도 변경
            childComments.forEach(Comment::changeStateToDelete);
        } else {
            // 자식 댓글이 없는 경우 실제 삭제
            repository.deleteById(commentId);
        }
    }


    @Override
    @Transactional(readOnly = true)
    public Comment viewComt(Long commentId) {
        return repository.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException(commentId));
    }

    @Override
    @Transactional(readOnly = true)
    public Comment viewComtWithUser(Long commentId) {
        return repository.findByIdWithUser(commentId)
                .orElseThrow(() -> new CommentNotFoundException(commentId));
    }

    @Override
    @Transactional(readOnly = true)
    public Comment viewComtWithParent(Long commentId) {
        return repository.findByIdWithParent(commentId)
                .orElseThrow(() -> new CommentNotFoundException(commentId));
    }

    @Override
    @Transactional(readOnly = true)
    public Comment viewComtWithUserAndParent(Long commentId) {
        return repository.findByIdWithUserAndParent(commentId)
                .orElseThrow(() -> new CommentNotFoundException(commentId));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Comment> viewComts(Long postId, Pageable pageable) {
        return repository.findByPostId(postId, pageable);
    }

    @Override
    public List<Comment> viewChildComts(Long parentCommendId) {
        return repository.findByParentCommentId(parentCommendId);
    }

    public boolean checkNonUserComment(Long commentId, String password) {
        Comment comment = repository.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException(commentId));

        log.info("comment.getPassword = {}, inputPassword = {}", comment.getPassword(), password);
        log.info("checkNonUser = {}", comment.getPassword().equals(password));
        return comment.getPassword().equals(password);
    }
}
