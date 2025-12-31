package com.example.taskapp.controller;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @GetMapping("/me")
    public Map<String, String> getMe(Authentication authentication) {
        if (authentication == null) {
            return Map.of("username", "anonymous");
        }
        return Map.of("username", authentication.getName());
    }
}