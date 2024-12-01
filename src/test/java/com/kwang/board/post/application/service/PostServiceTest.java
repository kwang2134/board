package com.kwang.board.post.application.service;

import com.kwang.board.post.domain.model.Post;
import com.kwang.board.post.domain.model.PostType;
import com.kwang.board.post.domain.repository.PostRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Slf4j
@ExtendWith(MockitoExtension.class)
class PostServiceTest {

    @Mock
    PostRepository repository;

    @InjectMocks
    PostService service;

    static final long POST_ID = 1L;
    Post post;

    @BeforeEach
    void setUp() {
        post = Post.builder()
                .id(POST_ID)
                .title("title")
                .content("content")
                .postType(PostType.NORMAL)
                .recomCount(0)
                .notRecomCount(0)
                .build();
    }

    @Test
    void changeToNormal() {
        //given
        post.changeTypeNotice();
        when(repository.findById(POST_ID)).thenReturn(Optional.of(post));

        //when
        service.changeToNormal(POST_ID);

        //then
        assertThat(post.getPostType()).isEqualTo(PostType.NORMAL);
        verify(repository).findById(POST_ID);
    }

    @Test
    void changeToNotice() {
        //given
        when(repository.findById(POST_ID)).thenReturn(Optional.of(post));

        //when
        service.changeToNotice(POST_ID);

        //then
        assertThat(post.getPostType()).isEqualTo(PostType.NOTICE);
        verify(repository).findById(POST_ID);
    }

    @Test
    void changeToPopular() {
        //given
        when(repository.findById(POST_ID)).thenReturn(Optional.of(post));

        //when
        service.changeToPopular(POST_ID);

        //then
        assertThat(post.getPostType()).isEqualTo(PostType.POPULAR);
        verify(repository).findById(POST_ID);
    }

}