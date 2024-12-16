package com.kwang.board.user.adapters.controller;

import com.kwang.board.post.adapters.mapper.PostMapper;
import com.kwang.board.post.application.service.PostService;
import com.kwang.board.post.domain.model.Post;
import com.kwang.board.user.adapters.mapper.UserMapper;
import com.kwang.board.user.adapters.security.userdetails.CustomUserDetails;
import com.kwang.board.user.application.dto.UserDTO;
import com.kwang.board.user.application.service.UserService;
import com.kwang.board.user.domain.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserFormController {

    private final UserService userService;
    private final PostService postService;

    private final UserMapper userMapper;
    private final PostMapper postMapper;

    //회원 가입
    @GetMapping("/signup")
    public String signupForm(Model model) {
        model.addAttribute("userRequest", new UserDTO.Request());
        return "user/signup-form";  // templates/users/signupForm.html
    }

    //로그인 -> SpringSecurity 처리
    @GetMapping("/login")
    public String loginForm(@RequestParam(required = false) String error, Model model) {
        if (error != null) {
            model.addAttribute("errorMessage", "아이디 또는 비밀번호가 일치하지 않습니다.");
        }
        return "user/login-form";
    }

    //마이페이지
    @GetMapping("/mypage")
    public String myPage(Model model, @AuthenticationPrincipal CustomUserDetails userDetails,
                         @PageableDefault(size = 8) Pageable pageable) {

        User user = userService.viewUserInfo(userDetails.getId());
        UserDTO.Request requestDTO = userMapper.toRequestDTO(user);
        Page<Post> posts = postService.viewUserPosts(userDetails.getId(), pageable);

        // 현재 페이지 (0부터 시작하므로 1을 더함)
        int currentPage = posts.getNumber() + 1;
        // 전체 페이지 수
        int totalPages = posts.getTotalPages();

        // 시작 페이지와 끝 페이지 계산
        int startPage = Math.max(1, currentPage - 4);
        int endPage = Math.min(totalPages, startPage + 8);

        // 시작 페이지 재조정 (끝 페이지가 최대값보다 작은 경우)
        startPage = Math.max(1, endPage - 8);

        model.addAttribute("user", userMapper.toDTO(user));  //유저 정보를 반환할 Response DTO
        model.addAttribute("userRequest", requestDTO);       //유저 정보 수정을 담을 Request DTO
        model.addAttribute("posts", postMapper.toUserInfoResponse(posts.getContent()));
        model.addAttribute("currentPage", currentPage);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("hasNext", posts.hasNext());
        model.addAttribute("hasPrev", posts.hasPrevious());
        return "user/mypage";
    }

    // posts 탭 페이징 처리
    @GetMapping("/mypage/posts")
    public String getUserPosts(@AuthenticationPrincipal CustomUserDetails userDetails,
                               @PageableDefault(size = 8) Pageable pageable,
                               Model model) {
        Page<Post> posts = postService.viewUserPosts(userDetails.getId(), pageable);

        int currentPage = posts.getNumber() + 1;
        int totalPages = posts.getTotalPages();

        int startPage = Math.max(1, currentPage - 4);
        int endPage = Math.min(totalPages, startPage + 8);
        startPage = Math.max(1, endPage - 8);

        model.addAttribute("posts", postMapper.toUserInfoResponse(posts.getContent()));
        model.addAttribute("currentPage", currentPage);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("hasNext", posts.hasNext());
        model.addAttribute("hasPrev", posts.hasPrevious());

        return "user/mypage :: #posts-fragment";  // posts 탭의 프래그먼트만 반환
    }
}
