package com.kwang.board.comment.adapters.controller;

import com.kwang.board.comment.application.service.CommentService;
import com.kwang.board.comment.domain.model.Comment;
import com.kwang.board.comment.domain.repository.CommentRepository;
import com.kwang.board.global.exception.exceptions.comment.CommentNotFoundException;
import com.kwang.board.post.domain.model.Post;
import com.kwang.board.post.domain.model.PostType;
import com.kwang.board.post.domain.repository.PostRepository;
import com.kwang.board.user.adapters.security.userdetails.CustomUserDetails;
import com.kwang.board.user.domain.model.User;
import com.kwang.board.user.domain.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
class CommentControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PostRepository postRepository;

    @Autowired
    CommentService commentService;

    @Autowired
    CommentRepository commentRepository;

    @Autowired
    WebApplicationContext context;

    User testUser;
    Post testPost;
    Comment parentComment;
    Comment childComment;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();
        commentRepository.deleteAll();
        postRepository.deleteAll();
        userRepository.deleteAll();

        testUser = userRepository.save(User.builder()
                .loginId("testuser")
                .password("password")
                .username("Test User")
                .email("test@example.com")
                .build());

        testPost = postRepository.save(Post.builder()
                .title("Test Post")
                .content("Test Content")
                .postType(PostType.NORMAL)
                .user(testUser)
                .build());

        // 부모 댓글 생성
        parentComment = Comment.builder()
                .content("Parent Comment")
                .displayName("Parent User")
                .build();

        // 자식 댓글 생성
        childComment = Comment.builder()
                .content("Child Comment")
                .displayName("Child User")
                .build();


        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .alwaysDo(result -> SecurityContextHolder.clearContext())
                .alwaysDo(print())
                .apply(springSecurity())
                .build();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
        commentRepository.deleteAll();
        postRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("회원 원본 댓글 작성 테스트")
    void testCreateOriginalCommentWithUser() throws Exception {
        // when
        // 댓글 작성 테스트 수행
        mockMvc.perform(post("/manage/post/{postId}/comment/write", testPost.getId())
                        .with(csrf())
                        .with(user(new CustomUserDetails(testUser)))
                        .param("content", "Original Comment")
                        .param("displayName", "Test User"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/post/" + testPost.getId()));

        // then
        List<Comment> comments = commentRepository.findAll();
        Comment comment = comments.get(0);

        assertThat(comments.size()).isEqualTo(1);
        assertThat(comment.getUser()).isNotNull();

    }

    @Test
    @DisplayName("회원 대댓글 작성 테스트")
    void testCreateReplyCommentWithUser() throws Exception {
        // when
        // 부모 댓글 저장
        Comment savedParent = commentService.createComt(parentComment, testPost.getId(), testUser.getId());

        mockMvc.perform(post("/manage/post/{postId}/comment/write", testPost.getId())
                        .with(csrf())
                        .with(user(new CustomUserDetails(testUser)))
                        .param("content", "Reply Comment")
                        .param("displayName", "Test User")
                        .param("parentId", savedParent.getId().toString()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/post/" + testPost.getId()));

        // then
        // 대댓글 저장 확인
        List<Comment> childComments = commentService.viewChildComts(savedParent.getId());
        assertThat(childComments).hasSize(1);
        assertThat(childComments.get(0).getParentComment().getId()).isEqualTo(savedParent.getId());

    }

    @Test
    @DisplayName("비회원 댓글 작성 테스트")
    void testCreateCommentWithoutUser() throws Exception {
        // when
        mockMvc.perform(post("/manage/post/{postId}/comment/write", testPost.getId())
                        .with(csrf())
                        .param("content", "Anonymous Comment")
                        .param("displayName", "Anonymous")
                        .param("password", "password123"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/post/" + testPost.getId()));

        // then
        // 댓글 저장 확인
        Page<Comment> comments = commentService.viewComts(testPost.getId(), PageRequest.of(0, 10));
        Comment savedComment = comments.getContent().get(0);
        assertThat(savedComment.getContent()).isEqualTo("Anonymous Comment");
        assertThat(savedComment.getUser()).isNull();
        assertThat(savedComment.getPassword()).isEqualTo("password123");
    }

    @Test
    @DisplayName("댓글 수정 테스트")
    void testUpdateComment() throws Exception {
        Comment savedComment = commentService.createComt(childComment, testPost.getId(), testUser.getId());

        // when
        mockMvc.perform(post("/manage/post/{postId}/comment/{commentId}/edit", testPost.getId(), savedComment.getId())
                        .with(csrf())
                        .with(user(new CustomUserDetails(testUser)))
                        .param("content", "Updated Comment")
                        .param("displayName", "Test User"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/post/" + testPost.getId()));

        Comment updatedComment = commentService.viewComt(savedComment.getId());

        // then
        assertThat(updatedComment.getContent()).isEqualTo("Updated Comment");

    }

    @Test
    @DisplayName("대댓글이 없는 댓글 삭제 테스트")
    void testDeleteCommentWithoutReplies() throws Exception {
        // given
        Comment savedComment = commentService.createComt(parentComment, testPost.getId(), testUser.getId());

        // when
        mockMvc.perform(post("/manage/post/{postId}/comment/{commentId}/delete",
                        testPost.getId(), savedComment.getId())
                        .with(user(new CustomUserDetails(testUser)))
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andDo(MockMvcResultHandlers.print())
                .andExpect(redirectedUrl("/post/" + testPost.getId()));

        // then
        assertThatThrownBy(() -> commentService.viewComt(savedComment.getId())).isInstanceOf(CommentNotFoundException.class);

    }

    @Test
    @DisplayName("대댓글이 있는 댓글 삭제 테스트")
    void testDeleteCommentWithReplies() throws Exception {
        // given
        // 부모 댓글 저장
        Comment savedParent = commentService.createComt(parentComment, testPost.getId(), testUser.getId());

        // 자식 댓글 저장
        childComment.connectParent(savedParent);
        Comment savedChild = commentService.createComt(childComment, testPost.getId(), testUser.getId());

        // when
        mockMvc.perform(post("/manage/post/{postId}/comment/{commentId}/delete",
                        testPost.getId(), savedParent.getId())
                        .with(user(new CustomUserDetails(testUser)))
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andDo(MockMvcResultHandlers.print())
                .andExpect(redirectedUrl("/post/" + testPost.getId()));

        // 상태 변경 확인
        Comment deletedParent = commentService.viewComt(savedParent.getId());
        Comment deletedChild = commentService.viewComt(savedChild.getId());
        assertThat(deletedParent.isDeleted()).isTrue();
        assertThat(deletedChild.isDeleted()).isTrue();

    }


    @Test
    @DisplayName("댓글 페이지 조회 테스트")
    void testGetCommentsByPage() throws Exception {
        // given
        commentService.createComt(childComment, testPost.getId(), testUser.getId());

        // when & then
        mockMvc.perform(get("/post/{postId}/comments/page", testPost.getId())
                        .param("pageGroup", "0"))
                .andExpect(status().isOk())
                .andExpect(view().name("post/view :: #commentsFragment"))
                .andExpect(model().attributeExists("comments", "commentRequest"));
    }

    @Test
    @DisplayName("회원 댓글 수정 폼 조회 테스트")
    void testGetCommentEditFormWithUser() throws Exception {
        // given
        Comment savedComment = commentService.createComt(parentComment, testPost.getId(), testUser.getId());

        // when & then
        mockMvc.perform(get("/post/{postId}/comment/{commentId}/edit",
                        testPost.getId(), savedComment.getId())
                        .with(user(new CustomUserDetails(testUser))))
                .andExpect(status().isOk())
                .andExpect(view().name("post/view :: #commentEditForm"))
                .andExpect(model().attributeExists("commentEditRequest"));

    }


    @Test
    @DisplayName("비회원 댓글 수정 폼 조회 테스트")
    void testGetCommentEditFormWithoutUser() throws Exception {
        // given
        Comment nonUserComment = Comment.builder()
                .content("Non-User Comment")
                .displayName("Anonymous")
                .password("password123")
                .build();
        Comment savedComment = commentService.createComt(nonUserComment, testPost.getId(), null);

        // when & then
        mockMvc.perform(get("/post/{postId}/comment/{commentId}/edit",
                        testPost.getId(), savedComment.getId())
                        .param("password", "password123"))
                .andExpect(status().isOk())
                .andExpect(view().name("post/view :: #commentEditForm"))
                .andExpect(model().attributeExists("commentEditRequest"));
    }

    @Test
    @DisplayName("권한 없는 회원의 댓글 수정 시도 테스트")
    void testUpdateCommentWithoutPermission() throws Exception {
        // Given
        // 다른 사용자의 댓글 생성
        User otherUser = userRepository.save(User.builder()
                .loginId("otheruser")
                .password("password")
                .username("Other User")
                .email("other@example.com")
                .build());
        Comment otherUserComment = commentService.createComt(parentComment, testPost.getId(), otherUser.getId());

        // When & Then
        mockMvc.perform(get("/post/{postId}/comment/{commentId}/edit",
                        testPost.getId(), otherUserComment.getId())
                        .with(csrf())
                        .with(user(new CustomUserDetails(testUser)))
                        .param("content", "Updated Comment")
                        .param("displayName", "Test User"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("댓글에 대한 수정 권한이 없습니다."));
    }

    @Test
    @DisplayName("권한 없는 회원의 댓글 삭제 시도 테스트")
    void testDeleteCommentWithoutPermission() throws Exception {
        // Given
        // 다른 사용자의 댓글 생성
        User otherUser = userRepository.save(User.builder()
                .loginId("otheruser")
                .password("password")
                .username("Other User")
                .email("other@example.com")
                .build());
        Comment otherUserComment = commentService.createComt(parentComment, testPost.getId(), otherUser.getId());

        // When & Then
        mockMvc.perform(post("/manage/post/{postId}/comment/{commentId}/delete",
                        testPost.getId(), otherUserComment.getId())
                        .with(csrf())
                        .with(user(new CustomUserDetails(testUser))))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("댓글에 대한 삭제 권한이 없습니다."));
    }


}

