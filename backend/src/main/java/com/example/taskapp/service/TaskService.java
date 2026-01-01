package com.example.taskapp.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.taskapp.dto.TaskRequest;
import com.example.taskapp.entity.Task;
import com.example.taskapp.entity.User;
import com.example.taskapp.exception.ResourceNotFoundException;
import com.example.taskapp.repository.TaskRepository;
import com.example.taskapp.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;

    private final UserRepository userRepository;

    /**
     * タスク情報をすべて取得する
     * 
     * @param username
     * @return タスクの全リスト
     */
    @Transactional(readOnly = true)
    public List<Task> getTasksByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));
        return taskRepository.findByUser(user);
    }

    /**
     * タスク作成
     * 
     * @param task
     * @param username
     * @return
     */
    @Transactional
    public Task createTaskWithUsername(Task task, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));
        task.setUser(user);
        task.setStatus("TODO");
        return taskRepository.save(task);
    }

    /**
     * タスク所有者のチェックを行う。
     * 
     * @param taskId
     * @param username
     * @return タスク
     */
    private Task getTaskIfOwner(Long taskId, String username) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));

        // タスクの所有者と実行ユーザーが一致するか確認
        if (!task.getUser().getUsername().equals(username)) {
            throw new AccessDeniedException("このタスクを操作する権限がありません");
        }
        return task;
    }

    /**
     * ステータスを更新する
     * 
     * @param id
     * @return 更新したタスク
     */
    @Transactional
    public void toggleTaskStatus(Long taskId, String username) {
        Task task = getTaskIfOwner(taskId, username);
        String newStatus = "TODO".equals(task.getStatus()) ? "DONE" : "TODO";
        task.setStatus(newStatus);
        taskRepository.save(task); // リポジトリのsaveを呼ぶ
    }

    /**
     * ステータスを削除する
     * 
     * @param id
     */
    @Transactional
    public void deleteById(Long taskId, String username) {
        getTaskIfOwner(taskId, username);
        taskRepository.deleteById(taskId);
    }
}
