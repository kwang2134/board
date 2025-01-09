package com.kwang.board.user.adapters.security;

import com.kwang.board.user.adapters.security.handler.admin.AdminAuthFailureHandler;
import com.kwang.board.user.adapters.security.handler.admin.AdminAuthSuccessHandler;
import com.kwang.board.user.adapters.security.handler.user.UserAuthFailureHandler;
import com.kwang.board.user.adapters.security.handler.user.UserAuthSuccessHandler;
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

    private final UserAuthFailureHandler failureHandler;
    private final UserAuthSuccessHandler successHandler;

    private final AdminAuthSuccessHandler adminSuccessHandler;
    private final AdminAuthFailureHandler adminFailureHandler;

    @Bean
    public SecurityFilterChain  userFilterChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher(request -> !request.getRequestURI().startsWith("/admin/"))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/user/mypage/**").authenticated()
                        .anyRequest().permitAll()
                )
                .formLogin(form -> form
                        .loginPage("/user/login")
                        .loginProcessingUrl("/user/login")
                        .successHandler(successHandler)
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
    public SecurityFilterChain adminFilterChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher("/admin/**")  // admin 관련 URL만 처리
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/admin/login").permitAll()
                        .anyRequest().hasRole("ADMIN")
                )
                .formLogin(form -> form
                        .loginPage("/admin/login")
                        .loginProcessingUrl("/admin/login")
                        .successHandler(adminSuccessHandler)
                        .failureHandler(adminFailureHandler)
                        .permitAll()
                )
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers("/api/**")
                );
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
