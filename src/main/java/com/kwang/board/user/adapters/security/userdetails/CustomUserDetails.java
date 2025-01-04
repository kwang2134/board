package com.kwang.board.user.adapters.security.userdetails;

import com.kwang.board.user.domain.model.Role;
import com.kwang.board.user.domain.model.User;
import lombok.Getter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collections;

@Getter
public class CustomUserDetails extends org.springframework.security.core.userdetails.User implements UserDetails {
    private final Long id;
    private final String displayName;
    private final Role role;

    public CustomUserDetails(User user) {
        super(user.getLoginId(), user.getPassword(), Collections.singleton(new SimpleGrantedAuthority("ROLE_" + user.getRole())));
        this.id = user.getId();
        this.displayName = user.getUsername();
        this.role = user.getRole();
    }
}
