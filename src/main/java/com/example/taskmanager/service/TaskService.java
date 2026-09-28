package com.example.taskmanager.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.taskmanager.TaskStatus;
import com.example.taskmanager.dto.TaskRequest;
import com.example.taskmanager.dto.TaskResponse;
import com.example.taskmanager.entity.Task;
import com.example.taskmanager.exception.TaskNotFoundException;
import com.example.taskmanager.mapper.TaskMapper;
import com.example.taskmanager.repository.TaskRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
@Service
public class TaskService {

	

	    private static final Logger log =
	            LoggerFactory.getLogger(TaskService.class);

	
	private final TaskRepository repository;
	private final TaskMapper mapper;
	
    public TaskService(
            TaskRepository repository,
            TaskMapper mapper) {

        this.repository = repository;
        this.mapper = mapper;
    }


    public List<Task> getAllTasks() {

        return repository.findAll();
    }

    public Task updateTask(Long id, Task task) {

        Task existingTask = repository.findById(id)
                .orElseThrow(() ->
                        new TaskNotFoundException(
                                "Task not found with id: " + id));

        existingTask.setTitle(task.getTitle());
        existingTask.setDescription(task.getDescription());
        existingTask.setStatus(task.getStatus());

        return repository.save(existingTask);
    }

    public void deleteTask(Long id) {
    	log.info("Deleting task with id: {}", id);

        Task existingTask = repository.findById(id)
                .orElseThrow(() ->
                        new TaskNotFoundException(
                                "Task not found with id: " + id));

        repository.delete(existingTask);
    }

    public Task getTaskById(Long id) {
    	log.info("Fetching task with id: {}", id);
        return repository.findById(id)
                .orElseThrow(() ->
                        new TaskNotFoundException(
                                "Task not found with id: " + id));
    }

    public Page<Task> getTasks(Pageable pageable) {

        return repository.findAll(pageable);
    }

    public List<Task> searchTasks(String title) {

        return repository.findByTitleContainingIgnoreCase(title);
    }

    public List<Task> getTasksByStatus(TaskStatus status) {

        return repository.findByStatus(status);
    }
    public TaskResponse createTask(TaskRequest request) {
    	log.info("Creating task with title: {}", request.getTitle());
        Task task = mapper.toEntity(request);
        Task savedTask = repository.save(task);
        return mapper.toResponse(savedTask);
    }
}