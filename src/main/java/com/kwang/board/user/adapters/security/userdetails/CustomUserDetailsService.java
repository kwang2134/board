package com.kwang.board.user.adapters.security.userdetails;

import com.kwang.board.user.domain.model.User;
import com.kwang.board.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository repository;

    @Override
    public UserDetails loadUserByUsername(String loginId) throws UsernameNotFoundException {
        User user = repository.findByLoginId(loginId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return new CustomUserDetails(user);
    }
}
