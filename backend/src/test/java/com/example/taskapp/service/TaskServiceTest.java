package com.example.taskapp.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import com.example.taskapp.dto.TaskRequest;
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
        when(taskRepository.findByUserUsernameOrAssigneeUsername("testuser", "testuser"))
                .thenReturn(List.of(new Task()));

        // Act
        List<Task> result = taskService.getTasksByUsername("testuser");

        // Assert
        assertThat(result).hasSize(1);
        verify(taskRepository).findByUserUsernameOrAssigneeUsername("testuser", "testuser");
    }

    @Test
    void createTask_WithAssignee_ShouldSaveTask() {
        // 1. 準備 (Given)
        TaskRequest request = new TaskRequest("New Task", "", 2L);

        User owner = new User();
        owner.setUsername("ownerUser");

        User assignee = new User();
        assignee.setId(2L);
        assignee.setUsername("assigneeUser");

        when(userRepository.findByUsername("ownerUser")).thenReturn(Optional.of(owner));
        when(userRepository.findById(2L)).thenReturn(Optional.of(assignee));
        when(taskRepository.save(any(Task.class))).thenAnswer(i -> i.getArguments()[0]);

        // 2. 実行 (When)
        Task result = taskService.createTask(request, "ownerUser");

        // 3. 検証 (Then)
        assertNotNull(result);
        assertEquals("New Task", result.getTitle());
        assertEquals(owner, result.getUser());
        assertEquals(assignee, result.getAssignee()); // アサインが正しいか
        assertEquals(TaskStatus.TODO, result.getStatus()); // ステータス変更の検証
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

    @Test
    @DisplayName("担当者なしでタスクを作成できること")
    void createTask_WithoutAssignee_ShouldSaveTask() {
        // Given
        TaskRequest request = new TaskRequest("New Task", "Description", null);
        User owner = new User();
        owner.setUsername("ownerUser");

        when(userRepository.findByUsername("ownerUser")).thenReturn(Optional.of(owner));
        when(taskRepository.save(any(Task.class))).thenAnswer(i -> i.getArguments()[0]);

        // When
        Task result = taskService.createTask(request, "ownerUser");

        // Then
        assertNotNull(result);
        assertEquals("New Task", result.getTitle());
        assertEquals(owner, result.getUser());
        assertNull(result.getAssignee());
        assertEquals(TaskStatus.ASSIGN_WAITING, result.getStatus());
    }

    @Test
    @DisplayName("担当者を更新できること")
    void updateAssignee_ShouldUpdateAssignee() {
        // Given
        Long taskId = 1L;
        Long assigneeId = 2L;
        User assignee = new User();
        assignee.setId(assigneeId);

        Task task = new Task();
        task.setId(taskId);
        task.setStatus(TaskStatus.ASSIGN_WAITING);

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        when(userRepository.findById(assigneeId)).thenReturn(Optional.of(assignee));
        when(taskRepository.save(any(Task.class))).thenReturn(task);

        // When
        Task result = taskService.updateAssignee(taskId, assigneeId);

        // Then
        assertEquals(assignee, result.getAssignee());
        assertEquals(TaskStatus.TODO, result.getStatus());
        verify(taskRepository).save(task);
    }

    @Test
    @DisplayName("担当者を解除できること")
    void updateAssignee_ShouldRemoveAssignee() {
        // Given
        Long taskId = 1L;
        Task task = new Task();
        task.setId(taskId);
        task.setAssignee(testUser);

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        when(taskRepository.save(any(Task.class))).thenReturn(task);

        // When
        Task result = taskService.updateAssignee(taskId, null);

        // Then
        assertNull(result.getAssignee());
        verify(taskRepository).save(task);
    }

    @Test
    @DisplayName("担当者更新時にタスクが見つからない場合に例外が発生すること")
    void updateAssignee_ThrowsException_WhenTaskNotFound() {
        // Given
        when(taskRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(RuntimeException.class, () -> taskService.updateAssignee(1L, 2L));
    }

    @Test
    @DisplayName("担当者更新時にユーザーが見つからない場合に例外が発生すること")
    void updateAssignee_ThrowsException_WhenAssigneeNotFound() {
        // Given
        Task task = new Task();
        task.setId(1L);

        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(userRepository.findById(2L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(RuntimeException.class, () -> taskService.updateAssignee(1L, 2L));
    }

    @Test
    @DisplayName("ステータスをDONEからTODOにトグルできること")
    void toggleTaskStatus_FromDoneToTodo() {
        // Given
        Long taskId = 1L;
        String username = "testuser";

        User user = new User();
        user.setUsername(username);

        Task task = new Task();
        task.setId(taskId);
        task.setStatus(TaskStatus.DONE);
        task.setUser(user);

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        when(taskRepository.save(any(Task.class))).thenReturn(task);

        // When
        taskService.toggleTaskStatus(taskId, username);

        // Then
        assertEquals(TaskStatus.TODO, task.getStatus());
        verify(taskRepository).save(task);
    }

    @Test
    @DisplayName("他人のタスクを削除しようとした場合、AccessDeniedExceptionが発生すること")
    void deleteTask_ThrowsException_WhenNotOwner() {
        // Given
        Long taskId = 1L;
        String username = "otheruser";

        User owner = new User();
        owner.setUsername("testuser");

        Task task = new Task();
        task.setId(taskId);
        task.setUser(owner);

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));

        // When & Then
        assertThrows(AccessDeniedException.class, () -> taskService.deleteById(taskId, username));
    }

    @Test
    @DisplayName("タスクをアサインできること")
    void assignTask_ShouldAssignTask() {
        // Given
        Long taskId = 1L;
        String assigneeUsername = "assigneeUser";

        User assignee = new User();
        assignee.setUsername(assigneeUsername);

        Task task = new Task();
        task.setId(taskId);

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        when(userRepository.findByUsername(assigneeUsername)).thenReturn(Optional.of(assignee));
        when(taskRepository.save(any(Task.class))).thenReturn(task);

        // When
        Task result = taskService.assignTask(taskId, assigneeUsername);

        // Then
        assertEquals(assignee, result.getAssignee());
        assertEquals(TaskStatus.PROGRESS, result.getStatus());
        verify(taskRepository).save(task);
    }

    @Test
    @DisplayName("アサイン時にタスクが見つからない場合に例外が発生すること")
    void assignTask_ThrowsException_WhenTaskNotFound() {
        // Given
        when(taskRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(RuntimeException.class, () -> taskService.assignTask(1L, "assignee"));
    }

    @Test
    @DisplayName("アサイン時にユーザーが見つからない場合に例外が発生すること")
    void assignTask_ThrowsException_WhenAssigneeNotFound() {
        // Given
        Task task = new Task();
        task.setId(1L);

        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(userRepository.findByUsername("assignee")).thenReturn(Optional.empty());

        // When & Then
        assertThrows(RuntimeException.class, () -> taskService.assignTask(1L, "assignee"));
    }
}