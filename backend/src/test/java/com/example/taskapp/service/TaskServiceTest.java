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

import java.util.Optional;

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

    @Test
    @DisplayName("ステータスがTODOからDONEに切り替わること")
    void toggleStatus_ToDone() {
        // Given
        Task task = new Task();
        task.setId(1L);
        task.setStatus("TODO");
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(taskRepository.save(any(Task.class))).thenAnswer(i -> i.getArguments()[0]);

        // When
        Task updatedTask = taskService.toggleTaskStatus(1L);

        // Then
        assertEquals("DONE", updatedTask.getStatus());
        verify(taskRepository).save(task);
    }

    @Test
    @DisplayName("タスクが削除されること")
    void deleteTask_Success() {
        // When
        taskService.deleteTask(1L);

        // Then
        verify(taskRepository, times(1)).deleteById(1L);
    }
}