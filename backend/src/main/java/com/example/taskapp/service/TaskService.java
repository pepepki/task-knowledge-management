package com.example.taskapp.service;

import java.util.List;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.taskapp.dto.TaskRequest;
import com.example.taskapp.entity.Task;
import com.example.taskapp.entity.User;
import com.example.taskapp.enums.TaskStatus;
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
        return taskRepository.findByUserUsernameOrAssigneeUsername(username, username);
    }

    /**
     * タスク作成
     * 
     * @param task
     * @param username
     * @return タスク
     */
    @Transactional
    public Task createTask(TaskRequest request, String ownerUsername) {
        // 1. 作成者（Owner）を取得
        User owner = userRepository.findByUsername(ownerUsername)
                .orElseThrow(() -> new ResourceNotFoundException("Owner not found"));

        Task task = new Task();
        task.setTitle(request.title());
        task.setDescription(request.description());
        task.setUser(owner);
        task.setStatus(TaskStatus.ASSIGN_WAITING);

        // 2. 担当者（Assignee）が指定されていればセット
        if (request.assigneeId() != null) {
            User assignee = userRepository.findById(request.assigneeId())
                    .orElseThrow(() -> new ResourceNotFoundException("Assignee not found"));
            task.setAssignee(assignee);
            task.setStatus(TaskStatus.TODO);
        }

        return taskRepository.save(task);
    }

    /**
     * タスクの担当者を更新する。
     * 
     * @param taskId
     * @param assigneeId
     * @return タスク
     */
    @Transactional
    public Task updateAssignee(Long taskId, Long assigneeId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));

        if (assigneeId != null) {
            User assignee = userRepository.findById(assigneeId)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found"));
            task.setAssignee(assignee);
            if (TaskStatus.ASSIGN_WAITING.equals(task.getStatus())) {
                task.setStatus(TaskStatus.TODO);
            }
        } else {
            task.setAssignee(null); // 担当者解除
        }

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
        TaskStatus newStatus = TaskStatus.DONE.equals(task.getStatus()) ? TaskStatus.TODO : TaskStatus.DONE;
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

    /**
     * タスクをアサインする
     * 
     * @param taskId
     * @param assigneeUsername
     * @return タスク
     */
    @Transactional
    public Task assignTask(Long taskId, String assigneeUsername) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));

        User assignee = userRepository.findByUsername(assigneeUsername)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        task.setAssignee(assignee);
        task.setStatus(TaskStatus.PROGRESS);
        return taskRepository.save(task);
    }
}
