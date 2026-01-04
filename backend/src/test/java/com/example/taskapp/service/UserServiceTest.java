package com.example.taskapp.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.taskapp.entity.User;
import com.example.taskapp.repository.UserRepository;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Map;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User testUser1;
    private User testUser2;

    @BeforeEach
    void setUp() {
        testUser1 = new User();
        testUser1.setId(1L);
        testUser1.setUsername("user1");

        testUser2 = new User();
        testUser2.setId(2L);
        testUser2.setUsername("user2");
    }

    @Test
    @DisplayName("全ユーザー情報を取得できること（パスワード除く）")
    void getAllUsersForAssignment_ReturnsListOfMaps() {
        // Arrange
        when(userRepository.findAll()).thenReturn(List.of(testUser1, testUser2));

        // Act
        List<Map<String, Object>> result = userService.getAllUsersForAssignment();

        // Assert
        assertThat(result).hasSize(2);

        Map<String, Object> firstUser = result.get(0);
        assertEquals(1L, firstUser.get("id"));
        assertEquals("user1", firstUser.get("username"));
        assertFalse(firstUser.containsKey("password"));

        Map<String, Object> secondUser = result.get(1);
        assertEquals(2L, secondUser.get("id"));
        assertEquals("user2", secondUser.get("username"));
        assertFalse(secondUser.containsKey("password"));

        verify(userRepository).findAll();
    }
}