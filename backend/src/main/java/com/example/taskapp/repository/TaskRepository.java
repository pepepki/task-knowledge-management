package com.example.taskapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.taskapp.entity.Task;

public interface TaskRepository extends JpaRepository<Task, Long> {

}