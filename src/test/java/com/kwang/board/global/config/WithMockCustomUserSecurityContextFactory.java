package com.kwang.board.global.config;

import com.kwang.board.user.adapters.security.userdetails.CustomUserDetails;
import com.kwang.board.user.domain.model.User;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import java.util.Collections;

public class WithMockCustomUserSecurityContextFactory implements WithSecurityContextFactory<WithMockCustomUser> {

    @Override
    public SecurityContext createSecurityContext(WithMockCustomUser customUser) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();

        User user = User.builder()
                .id(1L)
                .loginId(customUser.username())
                .password("password")
                .username("Test User")
                .email("test@example.com")
                .build();

        CustomUserDetails principal = new CustomUserDetails(user);

        Authentication auth = new UsernamePasswordAuthenticationToken(
                principal,
                null,
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + customUser.role()))
        );

        context.setAuthentication(auth);
        return context;
    }
}
