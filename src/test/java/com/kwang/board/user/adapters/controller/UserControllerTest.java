package com.kwang.board.user.adapters.controller;

import com.kwang.board.user.adapters.security.userdetails.CustomUserDetails;
import com.kwang.board.user.application.dto.UserUpdateDTO;
import com.kwang.board.user.application.service.UserService;
import com.kwang.board.user.domain.model.User;
import com.kwang.board.user.domain.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;
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
class UserControllerTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    UserService userService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    WebApplicationContext context;

    User testUser;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        testUser = User.builder()
                .loginId("testuser")
                .password("password")
                .username("Test User")
                .email("test@example.com")
                .build();

        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .alwaysDo(result -> SecurityContextHolder.clearContext())
                .alwaysDo(print())
                .apply(springSecurity())  // addFilter 대신 apply 사용
                .build();
    }


    @Test
    @WithAnonymousUser
    @DisplayName("회원 가입 폼 요청 테스트")
    void testSignupForm() throws Exception {
        mockMvc.perform(get("/user/signup"))
                .andExpect(status().isOk())
                .andExpect(handler().handlerType(UserFormController.class))
                .andExpect(handler().methodName("signupForm"))
                .andExpect(model().attributeExists("userRequest"));
    }

    @Test
    @DisplayName("회원 가입 처리 테스트")
    void testSignupUser() throws Exception {
        // when
        mockMvc.perform(post("/user/signup")
                        .with(csrf())
                        .param("loginId", "testuser")
                        .param("password", "password123")
                        .param("username", "Test User")
                        .param("email", "test@example.com"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));

        // then
        User savedUser = userRepository.findByLoginId("testuser").orElse(null);
        assertThat(savedUser).isNotNull();
        assertThat(savedUser.getUsername()).isEqualTo("Test User");
    }

    @Test
    @DisplayName("마이페이지 요청 테스트")
    void testMyPage() throws Exception {
        // given
        User user = userService.signupUser(testUser);

        // when & then
        mockMvc.perform(get("/user/mypage")
                        .with(csrf())
                        .with(user(new CustomUserDetails(user))))
                .andExpect(status().isOk())
                .andExpect(handler().handlerType(UserFormController.class))
                .andExpect(handler().methodName("myPage"))
                .andExpect(model().attributeExists("user", "userRequest", "posts"))
                .andReturn();

    }

    @Test
    @DisplayName("사용자 정보 수정 테스트")
    void testUpdateUser() throws Exception {
        // given
        User user = userService.signupUser(testUser);

        // when
        mockMvc.perform(post("/user/mypage")
                        .with(csrf())
                        .with(user(new CustomUserDetails(user)))
                        .param("username", "Updated User")
                        .param("email", "updated@example.com")
                        .param("password", "updatedPassword"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/user/mypage"));

        User updateUser = userService.updateUser(user.getId(), new UserUpdateDTO(
                "Updated User",
                "updated@example.com",
                "updatedPassword")
        );

        // then
        User findUser = userRepository.findByLoginId("testuser").orElse(null);
        assertThat(findUser).isNotNull();
        assertThat(findUser.getUsername()).isEqualTo("Updated User");
        assertThat(findUser.getEmail()).isEqualTo("updated@example.com");
        assertThat(findUser.getPassword()).isEqualTo(updateUser.getPassword());

    }
}

