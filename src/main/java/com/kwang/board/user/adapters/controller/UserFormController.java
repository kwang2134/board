package com.kwang.board.user.adapters.controller;

import com.kwang.board.post.adapters.mapper.PostMapper;
import com.kwang.board.post.application.service.PostService;
import com.kwang.board.user.application.dto.UserDTO;
import com.kwang.board.user.application.service.UserService;
import com.kwang.board.user.domain.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserFormController {

    private final UserService userService;
    private final PostService postService;

    private final PostMapper postMapper;

    //회원 가입
    @GetMapping("/signup")
    public String signupForm(Model model) {
        model.addAttribute("userDTO", new UserDTO.Request());
        return "users/signupForm";  // templates/users/signupForm.html
    }

    //로그인 -> SpringSecurity 처리
    @GetMapping("/login")
    public String loginForm() {
        return "users/loginForm";  // templates/users/loginForm.html
    }

    //마이페이지
    @GetMapping("/mypage")
    public String myPage(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = ((User) userDetails).getId();
        User user = userService.viewUserInfo(userId);

        model.addAttribute("user", user);
        model.addAttribute("posts", postMapper.toUserInfoResponse(user.getPosts()));
        return "users/myPage";
    }
}
