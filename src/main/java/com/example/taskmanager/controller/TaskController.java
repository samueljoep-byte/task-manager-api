package com.example.taskmanager.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import com.example.taskmanager.TaskStatus;
import com.example.taskmanager.dto.TaskRequest;
import com.example.taskmanager.dto.TaskResponse;
import com.example.taskmanager.entity.Task;
import com.example.taskmanager.service.TaskService;

@RestController
@RequestMapping("/tasks")
@io.swagger.v3.oas.annotations.security.SecurityRequirement(
        name = "bearerAuth"
)
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    // ADMIN only
    @PostMapping
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<TaskResponse> createTask(
            @Valid @RequestBody TaskRequest taskRequest) {

        TaskResponse savedTask =
                taskService.createTask(taskRequest);

        return new ResponseEntity<>(
                savedTask,
                HttpStatus.CREATED);
    }

    // USER and ADMIN
    @GetMapping
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<List<TaskResponse>> getAllTasks() {
        return ResponseEntity.ok(
                taskService.getAllTasks()
                        .stream()
                        .map(task -> {
                            TaskResponse response = new TaskResponse();

                            response.setId(task.getId());
                            response.setTitle(task.getTitle());
                            response.setDescription(task.getDescription());
                            response.setStatus(task.getStatus());
                            response.setCreatedAt(task.getCreatedAt());
                            response.setUpdatedAt(task.getUpdatedAt());

                            return response;
                        })
                        .toList()
        );
    }

    // USER and ADMIN
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<TaskResponse> getTaskById(
            @PathVariable Long id) {

        Task task = taskService.getTaskById(id);

        TaskResponse response = new TaskResponse();

        response.setId(task.getId());
        response.setTitle(task.getTitle());
        response.setDescription(task.getDescription());
        response.setStatus(task.getStatus());
        response.setCreatedAt(task.getCreatedAt());
        response.setUpdatedAt(task.getUpdatedAt());

        return ResponseEntity.ok(response);
    }

    // USER and ADMIN
   

    // ADMIN only
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<TaskResponse> updateTask(
            @PathVariable Long id,
            @Valid @RequestBody TaskRequest request) {

        return ResponseEntity.ok(
                taskService.updateTask(id, request));
    }
 // USER and ADMIN
    @GetMapping("/status/{status}")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<List<TaskResponse>> getTasksByStatus(
            @PathVariable TaskStatus status) {

        return ResponseEntity.ok(
                taskService.getTasksByStatus(status)
                        .stream()
                        .map(task -> {
                            TaskResponse response = new TaskResponse();

                            response.setId(task.getId());
                            response.setTitle(task.getTitle());
                            response.setDescription(task.getDescription());
                            response.setStatus(task.getStatus());
                            response.setCreatedAt(task.getCreatedAt());
                            response.setUpdatedAt(task.getUpdatedAt());

                            return response;
                        })
                        .toList()
        );
    }
    // ADMIN only
 // USER and ADMIN
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<String> deleteTask(
            @PathVariable Long id) {

        taskService.deleteTask(id);

        return ResponseEntity.ok(
                "Task deleted successfully");
    }
}