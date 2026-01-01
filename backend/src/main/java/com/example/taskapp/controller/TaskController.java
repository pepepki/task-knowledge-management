package com.example.taskapp.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.taskapp.dto.TaskRequest;
import com.example.taskapp.entity.Task;
import com.example.taskapp.entity.User;
import com.example.taskapp.service.TaskService;

import lombok.RequiredArgsConstructor;

/**
 * タスクの取得、編集を行うエンドポイント
 */
@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173") // React(Vite)のデフォルトポートを許可
public class TaskController {

    private final TaskService taskService;

    /**
     * タスクをすべて取得する
     * 
     * @param principal
     * @return タスクのリスト
     */
    @GetMapping
    public List<Task> getAllTasks(Principal principal) {
        return taskService.getTasksByUsername(principal.getName());
    }

    /**
     * タスクを作成する
     * 
     * @param task
     * @param principal
     * @return タスク
     */
    @PostMapping
    public Task createTask(@RequestBody Task task, Principal principal) {
        return taskService.createTaskWithUsername(task, principal.getName());
    }

    /**
     * タスクを更新する
     * 
     * @param id
     * @param principal
     * @return タスク
     */
    @PatchMapping("/{id}/toggle")
    public void toggleTask(@PathVariable Long id, Principal principal) {
        taskService.toggleTaskStatus(id, principal.getName());
    }

    /**
     * タスクを削除する
     * 
     * @param id
     * @param principal
     */
    @DeleteMapping("/{id}")
    public void deleteTask(@PathVariable Long id, Principal principal) {
        taskService.deleteById(id, principal.getName());
    }
}