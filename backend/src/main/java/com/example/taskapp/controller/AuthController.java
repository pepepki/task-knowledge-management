package com.example.taskapp.controller;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import com.example.taskapp.dto.SignupRequest;
import com.example.taskapp.entity.User;
import com.example.taskapp.repository.UserRepository;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthController(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * サインアップを行う
     * 
     * @param request
     * @return
     */
    @PostMapping("/signup")
    public String signup(@RequestBody SignupRequest request) {
        // ユーザー重複チェック
        if (userRepository.findByUsername(request.username()).isPresent()) {
            throw new RuntimeException("Username already exists");
        }

        User user = new User();
        user.setUsername(request.username());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setRole("ROLE_USER");
        
        userRepository.save(user);
        return "User registered successfully";
    }
}
