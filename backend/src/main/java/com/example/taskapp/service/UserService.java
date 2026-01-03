package com.example.taskapp.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.taskapp.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    /**
     * パスワードを除く全ユーザー情報を取得
     * 
     * @return 全ユーザー情報
     */
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getAllUsersForAssignment() {
        return userRepository.findAll().stream().map(user -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", user.getId());
            map.put("username", user.getUsername());
            return map;
        }).collect(Collectors.toList());
    }
}