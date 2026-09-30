package com.example.taskmanager.repository;

import com.example.taskmanager.entity.Task;
import com.example.taskmanager.entity.User;
import com.example.taskmanager.TaskStatus;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findByTitleContainingIgnoreCase(String title);

    List<Task> findByStatus(TaskStatus status);

    // Tasks belonging to a specific user
    List<Task> findByUser(User user);

    // Specific task belonging to a specific user
    Optional<Task> findByIdAndUser(Long id, User user);

    // Tasks with a specific status belonging to a specific user
    List<Task> findByStatusAndUser(TaskStatus status, User user);
}