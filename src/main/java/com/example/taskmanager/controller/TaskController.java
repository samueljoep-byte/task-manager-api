package com.example.taskmanager.controller;

import com.example.taskmanager.entity.Task;
import com.example.taskmanager.service.TaskService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.PutMapping;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import com.example.taskmanager.response.ApiResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RequestParam;
import com.example.taskmanager.TaskStatus;
import java.util.List;
import com.example.taskmanager.dto.TaskRequest;
import com.example.taskmanager.dto.TaskResponse;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    private final TaskService service;

    public TaskController(TaskService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<TaskResponse> createTask(
            @Valid @RequestBody TaskRequest request) {

        TaskResponse savedTask = service.createTask(request);

        return new ApiResponse<>(
                "Task created successfully",
                savedTask
        );
    }
    
    @GetMapping("/search")
    public ApiResponse<List<Task>> searchTasks(@RequestParam String title) {

        List<Task> tasks = service.searchTasks(title);

        return new ApiResponse<>(
                "Tasks found successfully",
                tasks
        );
    }
    @GetMapping
    public ApiResponse<Page<Task>> getAllTasks(Pageable pageable) {

        Page<Task> tasks = service.getTasks(pageable);

        return new ApiResponse<>(
                "Tasks retrieved successfully",
                tasks
        );
    }
        
        @GetMapping("/status/{status}")
        public ApiResponse<List<Task>> getTasksByStatus(
                @PathVariable TaskStatus status) {

            List<Task> tasks = service.getTasksByStatus(status);

            return new ApiResponse<>(
                    "Tasks filtered by status successfully",
                    tasks
            );
        }
        @GetMapping("/{id}")
        public ApiResponse<Task> getTaskById(@PathVariable Long id) {

            Task task = service.getTaskById(id);

            return new ApiResponse<>(
                    "Task retrieved successfully",
                    task
            );
        }
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTask(@PathVariable Long id) {
        service.deleteTask(id);
    }
    @PutMapping("/{id}")
    public ApiResponse<Task> updateTask(
            @PathVariable Long id,
            @Valid @RequestBody Task task) {

        Task updatedTask = service.updateTask(id, task);

        return new ApiResponse<>(
                "Task updated successfully",
                updatedTask
        );
    }
   
    }
