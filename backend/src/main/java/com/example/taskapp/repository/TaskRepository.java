package com.example.taskapp.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.taskapp.entity.Task;
import com.example.taskapp.entity.User;

public interface TaskRepository extends JpaRepository<Task, Long> {

    // ユーザーに基づいてタスクをフィルタリングするメソッド
    List<Task> findByUser(User user);

    // 作成者が自分、または担当者が自分であるタスクを取得
    List<Task> findByUserUsernameOrAssigneeUsername(String ownerUsername, String assigneeUsername);

}