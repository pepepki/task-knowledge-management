package com.example.taskapp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.taskapp.service.UserService;

import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173") // React(Vite)のデフォルトポートを許可
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public Map<String, String> getMe(Authentication authentication) {
        if (authentication == null) {
            return Map.of("username", "anonymous");
        }
        return Map.of("username", authentication.getName());
    }

    @GetMapping
    public List<Map<String, Object>> getAllUsers() {
        // IDとユーザー名だけを抽出して返す（パスワード漏洩防止）
        return userService.getAllUsersForAssignment();
    }
}