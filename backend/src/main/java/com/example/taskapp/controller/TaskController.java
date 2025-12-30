package com.example.taskapp.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.taskapp.entity.Task;
import com.example.taskapp.repository.TaskRepository;

/**
 * タスクの取得、編集を行うエンドポイント
 */
@RestController
@RequestMapping("/api/tasks")
@CrossOrigin(origins = "http://localhost:5173") // React(Vite)のデフォルトポートを許可
public class TaskController {

    @Autowired
    private TaskRepository taskRepository;

    /**
     * タスクをすべて取得する
     * 
     * @return タスクのリスト
     */
    @GetMapping
    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }
}