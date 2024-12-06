package com.kwang.board.post.adapters.controller;

import com.kwang.board.comment.application.service.CommentService;
import com.kwang.board.comment.domain.model.Comment;
import com.kwang.board.global.config.EmbeddedRedisConfig;
import com.kwang.board.global.exception.exceptions.comment.CommentNotFoundException;
import com.kwang.board.global.exception.exceptions.post.PostNotFoundException;
import com.kwang.board.photo.application.service.PhotoService;
import com.kwang.board.photo.domain.model.Photo;
import com.kwang.board.photo.domain.repository.PhotoRepository;
import com.kwang.board.post.application.dto.PostDTO;
import com.kwang.board.post.application.service.PostService;
import com.kwang.board.post.domain.model.Post;
import com.kwang.board.post.domain.model.PostType;
import com.kwang.board.post.domain.repository.PostRepository;
import com.kwang.board.user.adapters.security.userdetails.CustomUserDetails;
import com.kwang.board.user.domain.model.User;
import com.kwang.board.user.domain.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.io.File;
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

@SpringBootTest
@AutoConfigureMockMvc
@Import(EmbeddedRedisConfig.class)
class PostControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    PostService postService;

    @Autowired
    PhotoService photoService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PostRepository postRepository;

    @Autowired
    PhotoRepository photoRepository;

    @Autowired
    CommentService commentService;

    @Autowired
    RedisTemplate<String, String> redisTemplate;

    @Autowired
    WebApplicationContext context;

    User testUser;
    Post testPost;
    MockMultipartFile testImage;

    static final String UPLOAD_PATH = "D:\\project\\images";
    static final String TEST_SESSION_ID = "test-session-id";

    @BeforeEach
    void setUp() {
        File directory = new File(UPLOAD_PATH);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        postRepository.deleteAll();
        userRepository.deleteAll();
        RedisConnection connection = redisTemplate.getConnectionFactory().getConnection();
        connection.serverCommands().flushDb();
        connection.close();

        testUser = userRepository.save(User.builder()
                .loginId("testuser")
                .password("password")
                .username("Test User")
                .email("test@example.com")
                .build());

        testImage = new MockMultipartFile(
                "file",
                "test-image.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "test image content".getBytes()
        );

        MockHttpSession session = new MockHttpSession();
        session.setAttribute("sessionId", TEST_SESSION_ID);

        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .alwaysDo(result -> SecurityContextHolder.clearContext())
                .alwaysDo(print())
                .apply(springSecurity())
                .build();
    }

    @Test
    @DisplayName("회원 게시글 작성 테스트")
    void testCreatePostWithUser() throws Exception {
        // given
        // 이미지 임시 업로드
        String imagePath = photoService.tempUploadPhoto(testImage, TEST_SESSION_ID);

        // when
        mockMvc.perform(post("/manage/post/write")
                        .with(csrf())
                        .with(user(new CustomUserDetails(testUser)))
                        .param("title", "Test Title")
                        .param("content", "Test Content with " + imagePath)
                        .param("type", "NORMAL")
                        .param("displayName", "Test User"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));

        Post savedPost = postRepository.findAll().get(0);

        // then
        assertThat(savedPost.getTitle()).isEqualTo("Test Title");
        assertThat(savedPost.getUser()).isNotNull();
    }

    @Test
    @DisplayName("비회원 게시글 작성 테스트")
    void testCreatePostWithoutUser() throws Exception {
        // given
        // 이미지 임시 업로드
        String imagePath = photoService.tempUploadPhoto(testImage, TEST_SESSION_ID);

        // when
        mockMvc.perform(post("/manage/post/write")
                        .with(csrf())
                        .param("title", "Anonymous Post")
                        .param("content", "Anonymous Content " + imagePath)
                        .param("type", "NORMAL")
                        .param("displayName", "Anonymous")
                        .param("password", "password123"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));

        Post savedPost = postRepository.findAll().get(0);

        // then
        assertThat(savedPost.getTitle()).isEqualTo("Anonymous Post");
        assertThat(savedPost.getUser()).isNull();
    }

    @Test
    @DisplayName("게시글 수정 테스트")
    void testUpdatePost() throws Exception {
        // given
        Post savedPost = postService.createPost(Post.builder()
                .title("Original Title")
                .content("Original Content")
                .postType(PostType.NORMAL)
                .build(), testUser.getId());

        // when
        mockMvc.perform(post("/manage/post/{id}/edit", savedPost.getId())
                        .with(csrf())
                        .with(user(new CustomUserDetails(testUser)))
                        .param("title", "Updated Title")
                        .param("content", "Updated Content")
                        .param("type", "NORMAL"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/post/" + savedPost.getId()));

        Post updatedPost = postService.viewPost(savedPost.getId());

        // then
        assertThat(updatedPost.getTitle()).isEqualTo("Updated Title");
    }

    @Test
    @DisplayName("게시글 삭제 테스트 - 연관 데이터(댓글, 이미지) 삭제 확인")
    void testDeletePost() throws Exception {
        // given
        // 테스트용 이미지 파일 생성
        MockMultipartFile testImage = new MockMultipartFile(
                "file",
                "test-image.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "test image content".getBytes()
        );

        // 임시 이미지 업로드
        String imagePath = photoService.tempUploadPhoto(testImage, "test-session");

        // 이미지가 포함된 게시글 생성
        Post savedPost = postService.createPost(Post.builder()
                .title("Test Title")
                .content("Test Content with " + imagePath)
                .postType(PostType.NORMAL)
                .build(), testUser.getId());

        // 이미지 정식 등록
        List<Photo> savedPhotos = photoService.uploadPhoto(savedPost.getId(), "test-session");
        Photo savedPhoto = savedPhotos.get(0);

        // 회원 댓글 생성
        Comment memberComment = Comment.builder()
                .content("Member Comment")
                .displayName("Test User")
                .build();
        Comment savedMemberComment = commentService.createComt(memberComment, savedPost.getId(), testUser.getId());

        // 비회원 댓글 생성
        Comment nonMemberComment = Comment.builder()
                .content("Non-Member Comment")
                .displayName("Anonymous")
                .password("password123")
                .build();
        Comment savedNonMemberComment = commentService.createComt(nonMemberComment, savedPost.getId(), null);

        // when
        // 게시글 삭제 요청
        mockMvc.perform(post("/manage/post/{id}/delete", savedPost.getId())
                        .with(user(new CustomUserDetails(testUser)))
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));

        // then
        // 게시글 삭제 확인
        assertThatThrownBy(() -> postService.viewPost(savedPost.getId()))
                .isInstanceOf(PostNotFoundException.class);

        // 댓글 삭제 확인
        assertThatThrownBy(() -> commentService.viewComt(savedMemberComment.getId()))
                .isInstanceOf(CommentNotFoundException.class);
        assertThatThrownBy(() -> commentService.viewComt(savedNonMemberComment.getId()))
                .isInstanceOf(CommentNotFoundException.class);

        // 이미지 파일 삭제 확인
        File imageFile = new File(UPLOAD_PATH + savedPhoto.getSavedPhotoName());
        assertThat(imageFile.exists()).isFalse();

        // 이미지 DB 데이터 삭제 확인
        assertThat(photoRepository.findById(savedPhoto.getId())).isEmpty();
    }

    @Test
    @DisplayName("게시글 추천 테스트")
    void testRecommendPost() throws Exception {
        // given
        Post savedPost = postService.createPost(Post.builder()
                .title("Test Title")
                .content("Test Content")
                .postType(PostType.NORMAL)
                .build(), testUser.getId());

        // when
        mockMvc.perform(post("/api/post/{id}/recommend", savedPost.getId())
                        .with(user(new CustomUserDetails(testUser)))
                        .with(csrf()))
                .andExpect(status().isOk());

        // then
        Post recommendedPost = postService.viewPost(savedPost.getId());
        assertThat(recommendedPost.getRecomCount()).isEqualTo(1);
    }

    @Test
    @DisplayName("이미 추천한 게시글 재추천 시 실패 테스트")
    void testRecommendAlreadyRecommendedPost() throws Exception {
        // given
        // 테스트용 게시글 생성
        Post savedPost = postService.createPost(Post.builder()
                .title("Test Title")
                .content("Test Content")
                .postType(PostType.NORMAL)
                .build(), testUser.getId());

        CustomUserDetails user = new CustomUserDetails(testUser);

        // when
        // 첫 번째 추천 요청
        mockMvc.perform(post("/api/post/{id}/recommend", savedPost.getId())
                        .with(user(user))
                        .with(csrf()))
                .andExpect(status().isOk());

        // 동일 게시글 재추천 시도
        mockMvc.perform(post("/api/post/{id}/recommend", savedPost.getId())
                        .with(user(user))
                        .with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("이미 추천한 게시글입니다."));

        // then
        // 추천 수가 1로 유지되는지 확인
        Post post = postService.viewPost(savedPost.getId());
        assertThat(post.getRecomCount()).isEqualTo(1);
    }

    @Test
    @DisplayName("비추천한 게시글 추천 시 실패 테스트")
    void testRecommendAfterNotRecommend() throws Exception {
        // given
        // 테스트용 게시글 생성
        Post savedPost = postService.createPost(Post.builder()
                .title("Test Title")
                .content("Test Content")
                .postType(PostType.NORMAL)
                .build(), testUser.getId());

        CustomUserDetails user = new CustomUserDetails(testUser);

        // when
        // 비추천 요청
        mockMvc.perform(post("/api/post/{id}/not-recommend", savedPost.getId())
                        .with(user(user))
                        .with(csrf()))
                .andExpect(status().isOk());

        // 동일 게시글 추천 시도
        mockMvc.perform(post("/api/post/{id}/recommend", savedPost.getId())
                        .with(user(user))
                        .with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("이미 비추천한 게시글입니다."));

        // then
        // 비추천 수는 1이고 추천 수는 0인지 확인
        Post post = postService.viewPost(savedPost.getId());
        assertThat(post.getNotRecomCount()).isEqualTo(1);
        assertThat(post.getRecomCount()).isEqualTo(0);
    }


    @Test
    @DisplayName("게시글 검색 테스트")
    void testSearchPosts() throws Exception {
        // given
        // 테스트용 게시글 생성
        Post titlePost = postService.createPost(Post.builder()
                .title("Search Test Title")
                .displayName("Test User")
                .content("Normal Content")
                .postType(PostType.NORMAL)
                .build(), testUser.getId());

        Post contentPost = postService.createPost(Post.builder()
                .title("Normal Title")
                .displayName("Test User")
                .content("Search Test Content")
                .postType(PostType.NORMAL)
                .build(), testUser.getId());

        Post bothPost = postService.createPost(Post.builder()
                .title("Search Test Both")
                .displayName("Test User")
                .content("Search Test Both Content")
                .postType(PostType.NORMAL)
                .build(), testUser.getId());

        // when
        // 제목으로 검색
        MvcResult titleResult = mockMvc.perform(get("/posts/search")
                        .param("searchType", "TITLE")
                        .param("keyword", "Search"))
                .andExpect(status().isOk())
                .andExpect(view().name("posts/list :: #postsFragment"))
                .andExpect(model().attributeExists("posts"))
                .andReturn();

        // then
        List<PostDTO.ListResponse> titleSearchResults = (List<PostDTO.ListResponse>) titleResult
                .getModelAndView().getModel().get("posts");
        assertThat(titleSearchResults).hasSize(2)
                .extracting("title")
                .contains("Search Test Title", "Search Test Both");

        // when
        // 내용으로 검색
        MvcResult contentResult = mockMvc.perform(get("/posts/search")
                        .param("searchType", "CONTENT")
                        .param("keyword", "Search"))
                .andExpect(status().isOk())
                .andReturn();

        // then
        List<PostDTO.ListResponse> contentSearchResults = (List<PostDTO.ListResponse>) contentResult
                .getModelAndView().getModel().get("posts");
        assertThat(contentSearchResults).hasSize(2)
                .extracting("title")
                .contains("Normal Title", "Search Test Both");

        // when
        // 제목+내용으로 검색
        MvcResult bothResult = mockMvc.perform(get("/posts/search")
                        .param("searchType", "TITLE_CONTENT")
                        .param("keyword", "Search"))
                .andExpect(status().isOk())
                .andReturn();

        // then
        List<PostDTO.ListResponse> bothSearchResults = (List<PostDTO.ListResponse>) bothResult
                .getModelAndView().getModel().get("posts");
        assertThat(bothSearchResults).hasSize(3)
                .extracting("title")
                .contains("Search Test Title", "Normal Title", "Search Test Both");

        // when
        // 작성자로 검색
        MvcResult authorResult = mockMvc.perform(get("/posts/search")
                        .param("searchType", "AUTHOR")
                        .param("keyword", "Test User"))
                .andExpect(status().isOk())
                .andReturn();

        // then
        List<PostDTO.ListResponse> authorSearchResults = (List<PostDTO.ListResponse>) authorResult
                .getModelAndView().getModel().get("posts");
        assertThat(authorSearchResults).hasSize(3)
                .extracting("username")
                .containsOnly("Test User");
    }

    @AfterEach
    void tearDown() {
        RedisConnection connection = redisTemplate.getConnectionFactory().getConnection();
        connection.serverCommands().flushDb();
        connection.close();

        // 업로드된 파일 정리
        File directory = new File("D:\\project\\images");
        if (directory.exists()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    file.delete();
                }
            }
        }
    }

}