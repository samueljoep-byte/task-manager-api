package com.example.taskmanager.repository;

import com.example.taskmanager.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import com.example.taskmanager.TaskStatus;

public interface TaskRepository extends JpaRepository<Task, Long> {

	List<Task> findByTitleContainingIgnoreCase(String title);
	
	List<Task> findByStatus(TaskStatus status);
}