package com.kwang.board.comment.application.service;

import com.kwang.board.comment.application.dto.CommentUpdateDTO;
import com.kwang.board.comment.domain.model.Comment;
import com.kwang.board.comment.domain.repository.CommentRepository;
import com.kwang.board.comment.usecase.CommentCrudUseCase;
import com.kwang.board.global.exception.exceptions.comment.CommentNotFoundException;
import com.kwang.board.global.exception.exceptions.post.PostNotFoundException;
import com.kwang.board.post.domain.model.Post;
import com.kwang.board.post.domain.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
@Service
@RequiredArgsConstructor
public class CommentService implements CommentCrudUseCase {

    private final CommentRepository repository;
    private final PostRepository postRepository;

    @Override
    @Transactional
    public Comment createComt(Comment comt, Long parentId, Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException(postId));
        comt.connectPost(post);

        if (parentId != null) {
            Comment parentComment = repository.findById(parentId)
                    .orElseThrow(() -> new CommentNotFoundException(parentId));
            comt.connectParent(parentComment);
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
    public void deleteComt(Long comtId) {
        repository.deleteById(comtId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Comment> viewComts(Long postId) {
        return repository.findByPostId(postId);
    }
}
