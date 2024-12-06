package com.kwang.board.comment.adapters.controller;

import com.kwang.board.comment.application.service.CommentService;
import com.kwang.board.comment.domain.model.Comment;
import com.kwang.board.comment.domain.repository.CommentRepository;
import com.kwang.board.global.config.WithMockCustomUser;
import com.kwang.board.global.exception.exceptions.comment.CommentNotFoundException;
import com.kwang.board.post.domain.model.Post;
import com.kwang.board.post.domain.model.PostType;
import com.kwang.board.post.domain.repository.PostRepository;
import com.kwang.board.user.domain.model.User;
import com.kwang.board.user.domain.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 클래스 단위 테스트 불가
 */

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
                .apply(springSecurity())  // addFilter 대신 apply 사용
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
    @WithMockCustomUser
    void testCreateOriginalCommentWithUser() throws Exception {
        mockMvc.perform(post("/manage/post/{postId}/comment/write", testPost.getId())
                        .with(csrf())
                        .param("content", "Original Comment")
                        .param("displayName", "Test User"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/post/" + testPost.getId()));

        // 댓글 저장 확인
        Page<Comment> comments = commentService.viewComts(testPost.getId(), PageRequest.of(0, 10));
        Comment savedComment = comments.getContent().get(0);
        assertThat(savedComment.getContent()).isEqualTo("Original Comment");
        assertThat(savedComment.getUser()).isNotNull();
        assertThat(savedComment.getParentComment()).isNull();
    }

    @Test
    @DisplayName("회원 대댓글 작성 테스트")
    @WithMockCustomUser
    void testCreateReplyCommentWithUser() throws Exception {
        // 부모 댓글 저장
        Comment savedParent = commentService.createComt(parentComment, testPost.getId(), testUser.getId());

        mockMvc.perform(post("/manage/post/{postId}/comment/write", testPost.getId())
                        .with(csrf())
                        .param("content", "Reply Comment")
                        .param("displayName", "Test User")
                        .param("parentId", savedParent.getId().toString()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/post/" + testPost.getId()));

        // 대댓글 저장 확인
        List<Comment> childComments = commentService.viewChildComts(savedParent.getId());
        assertThat(childComments).hasSize(1);
        assertThat(childComments.get(0).getParentComment().getId()).isEqualTo(savedParent.getId());
    }

    @Test
    @DisplayName("비회원 댓글 작성 테스트")
    void testCreateCommentWithoutUser() throws Exception {
        mockMvc.perform(post("/manage/post/{postId}/comment/write", testPost.getId())
                        .with(csrf())
                        .param("content", "Anonymous Comment")
                        .param("displayName", "Anonymous")
                        .param("password", "password123"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/post/" + testPost.getId()));

        // 댓글 저장 확인
        Page<Comment> comments = commentService.viewComts(testPost.getId(), PageRequest.of(0, 10));
        Comment savedComment = comments.getContent().get(0);
        assertThat(savedComment.getContent()).isEqualTo("Anonymous Comment");
        assertThat(savedComment.getUser()).isNull();
        assertThat(savedComment.getPassword()).isEqualTo("password123");
    }

    @Test
    @DisplayName("댓글 수정 테스트")
    @WithMockCustomUser
    void testUpdateComment() throws Exception {
        Comment savedComment = commentService.createComt(childComment, testPost.getId(), testUser.getId());

        mockMvc.perform(post("/manage/post/{postId}/comment/{commentId}/edit", testPost.getId(), savedComment.getId())
                        .with(csrf())
                        .param("content", "Updated Comment")
                        .param("displayName", "Test User"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/post/" + testPost.getId()));

        Comment updatedComment = commentService.viewComt(savedComment.getId());
        assertThat(updatedComment.getContent()).isEqualTo("Updated Comment");
    }

    @Test
    @DisplayName("대댓글이 없는 댓글 삭제 테스트")
    @WithMockCustomUser
    void testDeleteCommentWithoutReplies() throws Exception {
        Comment savedComment = commentService.createComt(parentComment, testPost.getId(), testUser.getId());

        mockMvc.perform(post("/manage/post/{postId}/comment/{commentId}/delete",
                        testPost.getId(), savedComment.getId())
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/post/" + testPost.getId()));

        assertThatThrownBy(() -> commentService.viewComt(savedComment.getId())).isInstanceOf(CommentNotFoundException.class);
    }

    @Test
    @DisplayName("대댓글이 있는 댓글 삭제 테스트")
    @WithMockCustomUser
    void testDeleteCommentWithReplies() throws Exception {
        // 부모 댓글 저장
        Comment savedParent = commentService.createComt(parentComment, testPost.getId(), testUser.getId());

        // 자식 댓글 저장
        childComment.connectParent(savedParent);
        Comment savedChild = commentService.createComt(childComment, testPost.getId(), testUser.getId());

        mockMvc.perform(post("/manage/post/{postId}/comment/{commentId}/delete",
                        testPost.getId(), savedParent.getId())
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
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
        commentService.createComt(childComment, testPost.getId(), testUser.getId());

        mockMvc.perform(get("/post/{postId}/comments/page", testPost.getId())
                        .param("pageGroup", "0"))
                .andExpect(status().isOk())
                .andExpect(view().name("post/view :: #commentsFragment"))
                .andExpect(model().attributeExists("comments", "commentRequest"));
    }

    @Test
    @DisplayName("회원 댓글 수정 폼 조회 테스트")
    @WithMockCustomUser
    void testGetCommentEditFormWithUser() throws Exception {
        Comment savedComment = commentService.createComt(parentComment, testPost.getId(), testUser.getId());

        mockMvc.perform(get("/post/{postId}/comment/{commentId}/edit",
                        testPost.getId(), savedComment.getId()))
                .andExpect(status().isOk())
                .andExpect(view().name("post/view :: #commentEditForm"))
                .andExpect(model().attributeExists("commentEditRequest"));
    }

    @Test
    @DisplayName("비회원 댓글 수정 폼 조회 테스트")
    void testGetCommentEditFormWithoutUser() throws Exception {
        Comment nonUserComment = Comment.builder()
                .content("Non-User Comment")
                .displayName("Anonymous")
                .password("password123")
                .build();
        Comment savedComment = commentService.createComt(nonUserComment, testPost.getId(), null);

        mockMvc.perform(get("/post/{postId}/comment/{commentId}/edit",
                        testPost.getId(), savedComment.getId()))
                .andExpect(status().isOk())
                .andExpect(view().name("post/view :: #commentEditForm"))
                .andExpect(model().attributeExists("commentEditRequest"));
    }
}

