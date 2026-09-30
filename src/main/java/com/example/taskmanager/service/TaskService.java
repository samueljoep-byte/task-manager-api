package com.example.taskmanager.service;

import java.util.List;

import org.springframework.stereotype.Service;
import com.example.taskmanager.entity.User;
import com.example.taskmanager.TaskStatus;
import com.example.taskmanager.dto.TaskRequest;
import com.example.taskmanager.dto.TaskResponse;
import com.example.taskmanager.entity.Task;
import com.example.taskmanager.exception.TaskNotFoundException;
import com.example.taskmanager.mapper.TaskMapper;
import com.example.taskmanager.repository.TaskRepository;
import com.example.taskmanager.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
@Service
public class TaskService {

	private final TaskRepository repository;
	private final TaskMapper mapper;
	private final UserRepository userRepository;

	public TaskService(
	        TaskRepository repository,
	        TaskMapper mapper,
	        UserRepository userRepository) {

	    this.repository = repository;
	    this.mapper = mapper;
	    this.userRepository = userRepository;
	}

    // CREATE
	public TaskResponse createTask(TaskRequest request) {

	    String username = SecurityContextHolder
	            .getContext()
	            .getAuthentication()
	            .getName();

	    User user = userRepository.findByUsername(username)
	            .orElseThrow(() ->
	                    new RuntimeException("User not found"));

	    Task task = mapper.toEntity(request);

	    task.setUser(user);

	    Task savedTask = repository.save(task);

	    return mapper.toResponse(savedTask);
	}

    // GET ALL
	public List<Task> getAllTasks() {

	    String username = SecurityContextHolder
	            .getContext()
	            .getAuthentication()
	            .getName();

	    User user = userRepository.findByUsername(username)
	            .orElseThrow(() ->
	                    new RuntimeException("User not found"));

	    return repository.findByUser(user);
	}

    // GET BY ID
	public Task getTaskById(Long id) {

	    String username = SecurityContextHolder
	            .getContext()
	            .getAuthentication()
	            .getName();

	    User user = userRepository.findByUsername(username)
	            .orElseThrow(() ->
	                    new RuntimeException("User not found"));

	    return repository.findByIdAndUser(id, user)
	            .orElseThrow(() ->
	                    new TaskNotFoundException(
	                            "Task not found with id: " + id
	                    ));
	}

    // GET BY STATUS
	public List<Task> getTasksByStatus(TaskStatus status) {

	    String username = SecurityContextHolder
	            .getContext()
	            .getAuthentication()
	            .getName();

	    User user = userRepository.findByUsername(username)
	            .orElseThrow(() ->
	                    new RuntimeException("User not found"));

	    return repository.findByStatusAndUser(status, user);
	}

    // UPDATE
	public TaskResponse updateTask(
	        Long id,
	        TaskRequest request) {

	    String username = SecurityContextHolder
	            .getContext()
	            .getAuthentication()
	            .getName();

	    User user = userRepository.findByUsername(username)
	            .orElseThrow(() ->
	                    new RuntimeException("User not found"));

	    Task existingTask = repository.findByIdAndUser(id, user)
	            .orElseThrow(() ->
	                    new TaskNotFoundException(
	                            "Task not found with id: " + id
	                    ));

	    existingTask.setTitle(request.getTitle());
	    existingTask.setDescription(request.getDescription());
	    existingTask.setStatus(request.getStatus());

	    Task updatedTask = repository.save(existingTask);

	    return mapper.toResponse(updatedTask);
	}

    // DELETE
	public void deleteTask(Long id) {

	    String username = SecurityContextHolder
	            .getContext()
	            .getAuthentication()
	            .getName();

	    User user = userRepository.findByUsername(username)
	            .orElseThrow(() ->
	                    new RuntimeException("User not found"));

	    Task task = repository.findByIdAndUser(id, user)
	            .orElseThrow(() ->
	                    new TaskNotFoundException(
	                            "Task not found with id: " + id
	                    ));

	    repository.delete(task);
	}
}