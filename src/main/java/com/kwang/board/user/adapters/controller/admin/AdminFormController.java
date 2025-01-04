package com.kwang.board.user.adapters.controller.admin;

import com.kwang.board.user.adapters.mapper.AdminMapper;
import com.kwang.board.user.application.service.UserService;
import com.kwang.board.user.domain.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminFormController {

    private final UserService userService;

    private final AdminMapper adminMapper;

    @GetMapping("/login")
    public String adminLoginForm(@RequestParam(required = false) String error,
                                 Model model) {
        if (error != null) {
            model.addAttribute("errorMessage", "관리자 계정 정보가 올바르지 않습니다.");
        }
        return "admin/login-form";
    }

    @GetMapping("/manage")
    public String manageUser(Model model) {
        List<User> normalUsers = userService.getNormalUsers();
        List<User> manageUsers = userService.getManageUsers();

        model.addAttribute("normalUsers", adminMapper.toUserList(normalUsers));
        model.addAttribute("managerUsers", adminMapper.toUserList(manageUsers));

        log.info("manage 페이지 호출 성공");

        return "admin/manage-form";
    }
}
