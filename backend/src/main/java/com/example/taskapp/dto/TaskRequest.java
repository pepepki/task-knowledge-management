package com.example.taskapp.dto;

/**
 * タスク登録リクエストを格納するRecord
 */

public record TaskRequest(
        String title,
        String description,
        Long assigneeId) {
}