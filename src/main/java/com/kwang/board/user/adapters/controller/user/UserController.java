package com.kwang.board.user.adapters.controller.user;

import com.kwang.board.global.validation.ValidationGroups;
import com.kwang.board.post.adapters.mapper.PostMapper;
import com.kwang.board.post.application.service.PostService;
import com.kwang.board.user.adapters.mapper.UserMapper;
import com.kwang.board.user.adapters.security.userdetails.CustomUserDetails;
import com.kwang.board.user.application.dto.user.UserDTO;
import com.kwang.board.user.application.service.UserService;
import com.kwang.board.user.domain.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final PostService postService;

    private final UserMapper userMapper;
    private final PostMapper postMapper;

    //회원 가입
    @PostMapping("/signup")
    public String signupUser(@Validated(ValidationGroups.SignUp.class) @ModelAttribute("userRequest") UserDTO.Request userRequest,
                             BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return "user/signup-form";
        }

        User user = userService.signupUser(userMapper.toEntity(userRequest));
        return "redirect:/";
    }

    //정보 수정
    @PostMapping("/mypage")
    public String updateUser(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Validated(ValidationGroups.Update.class) @ModelAttribute UserDTO.Request userRequest,
            BindingResult bindingResult,
            Model model) {

        if (bindingResult.hasErrors()) {
            // 검증 실패 시 필요한 데이터를 다시 모델에 추가
            User user = userService.viewUserInfo(userDetails.getId());
            model.addAttribute("user", userMapper.toDTO(user));
            model.addAttribute("userRequest", userRequest);

            // edit 탭이 활성화되도록 상태 전달
            model.addAttribute("activeTab", "edit");

            return "user/mypage";
        }

        userService.updateUser(userDetails.getId(), userMapper.toUpdateDTO(userRequest));
        return "redirect:/user/mypage";
    }
}
