package com.example.taskapp.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.taskapp.dto.TaskRequest;
import com.example.taskapp.entity.Task;
import com.example.taskapp.repository.TaskRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;

    /**
     * タスク情報をすべて取得する
     * @return タスクの全リスト
     */
    @Transactional(readOnly = true)
    public List<Task> getAllTasks(){
        return taskRepository.findAll();
    }

    /**
     * タスク情報を新規登録する。
     * @param request
     * @return 登録したタスク
     */
    @Transactional
    public Task createTask(TaskRequest request) {
        Task task = new Task();
        task.setTitle(request.title());
        task.setDescription(request.description());
        task.setStatus("TODO");
        return taskRepository.save(task);
    }
}
