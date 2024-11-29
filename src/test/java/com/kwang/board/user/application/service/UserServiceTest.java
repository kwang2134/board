package com.kwang.board.user.application.service;

import com.kwang.board.global.exception.exceptions.user.InvalidLoginException;
import com.kwang.board.user.domain.model.Role;
import com.kwang.board.user.domain.model.User;
import com.kwang.board.user.domain.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Slf4j
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    UserRepository repository;

    @InjectMocks
    UserService service;

    static final String LOGIN_ID = "asdf1234";
    static final String PASSWORD = "1234";
    User user;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .loginId(LOGIN_ID)
                .password(PASSWORD)
                .role(Role.USER)
                .build();
    }

    @Test
    void login_Success() {
        // given
        when(repository.findByLoginId(LOGIN_ID)).thenReturn(Optional.of(user));

        // when
        User loginUser = service.login(LOGIN_ID, PASSWORD);

        // then
        assertThat(loginUser).isEqualTo(user);
        verify(repository).findByLoginId(LOGIN_ID);
    }

    @Test
    void login_FailedWrongId() {
        // given
        when(repository.findByLoginId("wrong")).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> service.login("wrong", PASSWORD)).isInstanceOf(InvalidLoginException.class);
    }

    @Test
    void login_FailedWrongPassword() {
        // given
        when(repository.findByLoginId(LOGIN_ID)).thenReturn(Optional.of(user));

        // when & then
        assertThatThrownBy(() -> service.login(LOGIN_ID, "wrong")).isInstanceOf(InvalidLoginException.class);
    }
}