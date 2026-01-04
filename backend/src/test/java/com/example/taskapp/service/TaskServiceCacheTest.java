package com.example.taskapp.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import com.example.taskapp.entity.Task;
import com.example.taskapp.repository.TaskRepository;

@SpringBootTest
@ActiveProfiles("test")
public class TaskServiceCacheTest {

    @Autowired
    private TaskService taskService;

    @MockitoBean
    private TaskRepository taskRepository;

    @Autowired
    private CacheManager cacheManager;

    @Test
    @DisplayName("キャッシュされていること")
    void getTasksByUsername_shouldCacheResult() {
        String username = "testUser";
        List<Task> mockTasks = List.of(new Task());

        when(taskRepository.findByUserUsernameOrAssigneeUsername(username, username)).thenReturn(mockTasks);

        List<Task> result1 = taskService.getTasksByUsername(username);

        List<Task> result2 = taskService.getTasksByUsername(username);

        verify(taskRepository, times(1)).findByUserUsernameOrAssigneeUsername(username, username);

        assertEquals(result1.size(), result2.size());
        assertEquals(result1.get(0).getTitle(), result2.get(0).getTitle());
    }

    @BeforeEach
    void clearCache() {
        // テストごとにキャッシュをクリア
        cacheManager.getCache("tasks").clear();
    }
}
