package com.example.taskapp.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import com.example.taskapp.entity.Task;
import com.example.taskapp.entity.User;
import com.example.taskapp.enums.TaskStatus;
import com.example.taskapp.repository.TaskRepository;
import com.example.taskapp.repository.UserRepository;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private TaskService taskService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
    }

    @Test
    @DisplayName("ユーザー名指定でタスク一覧が取得できること")
    void getTasksByUsername_ReturnsList() {
        // Arrange
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(taskRepository.findByUser(testUser)).thenReturn(List.of(new Task()));

        // Act
        List<Task> result = taskService.getTasksByUsername("testuser");

        // Assert
        assertThat(result).hasSize(1);
        verify(userRepository).findByUsername("testuser");
        verify(taskRepository).findByUser(testUser);
    }

    @Test
    @DisplayName("タスク作成時にユーザーが正しく紐付けられること")
    void createTaskWithUsername_SavesTaskWithUser() {
        // Arrange
        Task inputTask = new Task();
        inputTask.setTitle("New Task");

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(taskRepository.save(any(Task.class))).thenAnswer(i -> i.getArguments()[0]);

        // Act
        Task savedTask = taskService.createTaskWithUsername(inputTask, "testuser");

        // Assert
        assertThat(savedTask.getUser()).isEqualTo(testUser); // ユーザーがセットされているか
        assertThat(savedTask.getTitle()).isEqualTo("New Task");
        verify(taskRepository).save(any(Task.class));
    }

    @Test
    @DisplayName("所有者であればステータスをTODOからDONEにトグルできること")
    void toggleTaskStatus_ToDone() {
        // Arrange
        Long taskId = 1L;
        String username = "testuser";

        // 実行ユーザー（所有者）の作成
        User user = new User();
        user.setUsername(username);

        Task task = new Task();
        task.setId(taskId);
        task.setStatus(TaskStatus.TODO);
        task.setUser(user); // 所有者をセット

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        when(taskRepository.save(any(Task.class))).thenReturn(task);

        // Act
        taskService.toggleTaskStatus(taskId, username);

        // Assert
        assertThat(task.getStatus()).isEqualTo(TaskStatus.DONE);
        verify(taskRepository).save(task);
    }

    @Test
    @DisplayName("所有者であればタスクを削除できること")
    void deleteTask_Success() {
        // Arrange
        Long taskId = 1L;
        String username = "testuser";

        User user = new User();
        user.setUsername(username);

        Task task = new Task();
        task.setId(taskId);
        task.setUser(user); // 所有者をセット

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));

        // Act
        taskService.deleteById(taskId, username); // ユーザー名を渡す

        // Assert
        verify(taskRepository, times(1)).deleteById(taskId);
    }

    @Test
    @DisplayName("存在しないタスクを削除しようとした場合に例外が発生すること")
    void deleteTask_ThrowsException_WhenNotFound() {
        // Arrange
        Long taskId = 99L;
        String username = "testuser";

        // Repositoryが空のOptionalを返す（タスクが見つからない）設定
        when(taskRepository.findById(taskId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            taskService.deleteById(taskId, username);
        });

        // 削除メソッド自体は呼ばれていないことを検証
        verify(taskRepository, never()).deleteById(anyLong());
    }

    @Test
    @DisplayName("他人のタスクをトグルしようとした場合、AccessDeniedExceptionが発生すること")
    void toggleTaskStatus_ThrowsException_WhenNotOwner() {

        Long taskId = 1L;
        User owner = new User();
        owner.setUsername("owner_user");

        Task task = new Task();
        task.setUser(owner);

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));

        // Act & Assert
        // "other_user" が操作しようとするケース
        assertThrows(AccessDeniedException.class, () -> {
            taskService.toggleTaskStatus(taskId, "other_user");
        });
    }
}