package com.example.taskapp.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.taskapp.dto.TaskRequest;
import com.example.taskapp.entity.Task;
import com.example.taskapp.exception.ResourceNotFoundException;
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

    /**
     * ステータスを更新する
     * 
     * @param id
     * @return 更新したタスク
     */
    @Transactional
    public Task toggleTaskStatus(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));
        
        // TODO <-> DONE を切り替える
        String newStatus = "DONE".equals(task.getStatus()) ? "TODO" : "DONE";
        task.setStatus(newStatus);
        return taskRepository.save(task);
    }

    /**
     * ステータスを削除する
     * 
     * @param id
     */
    @Transactional
    public void deleteTask(Long id) {
        taskRepository.deleteById(id);
    }
}
