package com.example.taskapp.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.taskapp.dto.TaskRequest;
import com.example.taskapp.entity.Task;
import com.example.taskapp.repository.TaskRepository;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private TaskService taskService;

    @Test
    @DisplayName("タスクが正常に保存されること")
    void createTask_Success() {
        // Given: Java 25 record を作成
        TaskRequest request = new TaskRequest("テストタイトル", "テスト詳細");
        Task savedTask = new Task();
        savedTask.setId(1L);
        savedTask.setTitle(request.title());

        when(taskRepository.save(any(Task.class))).thenReturn(savedTask);

        // When
        Task result = taskService.createTask(request);

        // Then
        assertNotNull(result);
        assertEquals("テストタイトル", result.getTitle());
        verify(taskRepository, times(1)).save(any(Task.class));
    }
}