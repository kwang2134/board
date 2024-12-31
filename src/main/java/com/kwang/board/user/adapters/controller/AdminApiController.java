package com.kwang.board.user.adapters.controller;

import com.kwang.board.user.application.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/manage")
public class AdminApiController {

    private final UserService userService;

    @PostMapping("/ban")
    public ResponseEntity<?> banUser(@RequestParam Long id) {
        try {
            userService.banUser(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/appoint")
    public ResponseEntity<?> appointUser(@RequestParam Long id) {
        try {
            userService.appointUser(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/demote")
    public ResponseEntity<?> demoteUser(@RequestParam Long id) {
        try {
            userService.demoteUser(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}
