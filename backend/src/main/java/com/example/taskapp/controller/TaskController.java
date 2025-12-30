package com.example.taskapp.controller;

import java.util.List;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.taskapp.dto.TaskRequest;
import com.example.taskapp.entity.Task;
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
     * @return タスクのリスト
     */
    @GetMapping
    public List<Task> getAllTasks() {
        return taskService.getAllTasks();
    }

    @PostMapping
    public Task createTask(@RequestBody TaskRequest request) {
        return taskService.createTask(request);
    }
}