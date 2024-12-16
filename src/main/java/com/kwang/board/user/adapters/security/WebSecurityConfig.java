package com.kwang.board.user.adapters.security;

import com.kwang.board.user.adapters.security.handler.CustomAuthenticationFailureHandler;
import com.kwang.board.user.adapters.security.handler.LoginSuccessHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurityConfig {

    private final CustomAuthenticationFailureHandler failureHandler;
    private final LoginSuccessHandler loginSuccessHandler;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/user/signup", "/user/login").permitAll()
                        .requestMatchers("/post/{postId}/comment/**", "/post/{postId}/comment/{commentId}/**", "/post/**").permitAll()
                        .requestMatchers("/posts/**", "/post/**").permitAll()
                        .requestMatchers("/api/**").permitAll()
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .requestMatchers("/css/**", "/js/**", "/images/**").permitAll()
                        .requestMatchers("/error").permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/user/login")
                        .successHandler(loginSuccessHandler)
                        .failureHandler(failureHandler)
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/user/logout")
                        .logoutSuccessUrl("/")
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
                        .permitAll()
                )
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers("/api/**")  // API 요청은 CSRF 제외
                );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
