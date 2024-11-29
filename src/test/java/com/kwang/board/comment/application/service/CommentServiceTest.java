package com.kwang.board.comment.application.service;

import com.kwang.board.comment.domain.model.Comment;
import com.kwang.board.comment.domain.repository.CommentRepository;
import com.kwang.board.global.exception.exceptions.comment.CommentNotFoundException;
import com.kwang.board.global.exception.exceptions.post.PostNotFoundException;
import com.kwang.board.post.domain.model.Post;
import com.kwang.board.post.domain.model.PostType;
import com.kwang.board.post.domain.repository.PostRepository;
import com.kwang.board.user.domain.model.Role;
import com.kwang.board.user.domain.model.User;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Slf4j
@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    @Mock
    CommentRepository repository;

    @Mock
    PostRepository postRepository;

    @InjectMocks
    CommentService service;

    static final long USER_ID = 1L;
    static final Long POST_ID = 1L;
    static final Long PARENT_ID = 1L;
    static final Long COMMENT_ID = 2L;

    Comment comment;
    Comment parentComment;
    User user;
    Post post;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(USER_ID)
                .loginId("LOGIN_ID")
                .password("PASSWORD")
                .role(Role.USER)
                .build();

        post = Post.builder()
                .id(POST_ID)
                .title("post title")
                .content("post content")
                .postType(PostType.NORMAL)
                .recomCount(0)
                .notRecomCount(0)
                .user(user)
                .build();

        parentComment = Comment.builder()
                .id(PARENT_ID)
                .content("parent content")
                .user(user)
                .post(post)
                .build();

        comment = Comment.builder()
                .id(COMMENT_ID)
                .content("comment content")
                .user(user)
                .post(post)
                .build();

    }

    @Test
    void createComment_PostNotFound() {
        // given
        when(postRepository.findById(POST_ID)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> service.createComt(comment, null, POST_ID))
                .isInstanceOf(PostNotFoundException.class);
    }


    @Test
    void createComment_WithoutParent() {
        //given
        when(postRepository.findById(POST_ID)).thenReturn(Optional.of(post));
        when(repository.save(any(Comment.class))).thenReturn(comment);

        //when
        Comment savedComment = service.createComt(comment, null, POST_ID);

        //then
        assertThat(savedComment).isEqualTo(comment);
        assertThat(comment.getParentComment()).isNull();
        verify(postRepository).findById(POST_ID);
        verify(repository).save(comment);
    }

    @Test
    void createComment_WithParent() {
        //given
        when(repository.findById(PARENT_ID)).thenReturn(Optional.of(parentComment));
        when(postRepository.findById(POST_ID)).thenReturn(Optional.of(post));
        when(repository.save(any(Comment.class))).thenReturn(comment);

        //when
        Comment savedComment = service.createComt(comment, PARENT_ID, POST_ID);

        //then
        assertThat(comment.getParentComment()).isEqualTo(parentComment);
        assertThat(savedComment).isEqualTo(comment);
        verify(postRepository).findById(POST_ID);
        verify(repository).findById(PARENT_ID);
    }

    @Test
    void createComment_WithParentWrongId() {
        //given
        when(repository.findById(PARENT_ID)).thenReturn(Optional.empty());
        when(postRepository.findById(POST_ID)).thenReturn(Optional.of(post));

        //when & then
        assertThatThrownBy(() -> service.createComt(comment, PARENT_ID, POST_ID))
                .isInstanceOf(CommentNotFoundException.class);
    }

    @Test
    void viewComments() {
        //given
        when(repository.findByPostId(POST_ID)).thenReturn(List.of(comment, parentComment));

        //when
        List<Comment> comments = service.viewComts(POST_ID);

        //then
        assertThat(comments.size()).isEqualTo(2);
        verify(repository).findByPostId(POST_ID);
    }

    @Test
    void viewComments_EmptyList() {
        // given
        when(repository.findByPostId(POST_ID)).thenReturn(Collections.emptyList());

        // when
        List<Comment> comments = service.viewComts(POST_ID);

        // then
        assertThat(comments).isEmpty();
        verify(repository).findByPostId(POST_ID);
    }
}