package com.kwang.board.user.adapters.controller;

import com.kwang.board.post.adapters.mapper.PostMapper;
import com.kwang.board.post.application.service.PostService;
import com.kwang.board.user.adapters.mapper.UserMapper;
import com.kwang.board.user.adapters.security.userdetails.CustomUserDetails;
import com.kwang.board.user.application.dto.UserDTO;
import com.kwang.board.user.application.service.UserService;
import com.kwang.board.user.domain.model.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/manage/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final PostService postService;

    private final UserMapper userMapper;
    private final PostMapper postMapper;

    //회원 가입
    @PostMapping("/signup")
    public String signupUser(@Valid @ModelAttribute UserDTO.Request userRequest) {
        User user = userService.signupUser(userMapper.toEntity(userRequest));
        return "redirect:/";
    }

    //정보 수정
    @PostMapping("/mypage")
    public String updateUser(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @ModelAttribute UserDTO.Request userRequest) {
        userService.updateUser(userDetails.getId(), userMapper.toUpdateDTO(userRequest));
        return "redirect:/user/mypage";  // 마이페이지로 리다이렉트
    }
}
