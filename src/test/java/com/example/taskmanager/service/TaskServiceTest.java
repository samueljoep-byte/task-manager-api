package com.example.taskmanager.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.taskmanager.TaskStatus;
import com.example.taskmanager.dto.TaskRequest;
import com.example.taskmanager.dto.TaskResponse;
import com.example.taskmanager.entity.Task;
import com.example.taskmanager.exception.TaskNotFoundException;
import com.example.taskmanager.mapper.TaskMapper;
import com.example.taskmanager.repository.TaskRepository;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository repository;

    @Mock
    private TaskMapper mapper;

    @InjectMocks
    private TaskService service;


    @Test
    void testCreateTask() {

        TaskRequest request = new TaskRequest();

        request.setTitle("Learn Spring");
        request.setDescription("Study Spring Boot");
        request.setStatus(TaskStatus.PENDING);

        Task task = new Task();

        task.setTitle("Learn Spring");
        task.setDescription("Study Spring Boot");
        task.setStatus(TaskStatus.PENDING);

        TaskResponse response = new TaskResponse();

        response.setTitle("Learn Spring");
        response.setDescription("Study Spring Boot");
        response.setStatus(TaskStatus.PENDING);

        when(mapper.toEntity(request)).thenReturn(task);

        when(repository.save(task)).thenReturn(task);

        when(mapper.toResponse(task)).thenReturn(response);

        TaskResponse result = service.createTask(request);

        assertEquals("Learn Spring", result.getTitle());
    }

    @Test
    void testGetTaskByIdNotFound() {

        when(repository.findById(99L))
                .thenReturn(Optional.empty());

        TaskNotFoundException exception =
                assertThrows(
                        TaskNotFoundException.class,
                        () -> service.getTaskById(99L)
                );

        assertEquals(
                "Task not found with id: 99",
                exception.getMessage()
        );

        verify(repository).findById(99L);
    }


    @Test
    void testUpdateTask() {

        Task existingTask = new Task();

        existingTask.setId(1L);
        existingTask.setTitle("Old Title");
        existingTask.setDescription("Old Description");
        existingTask.setStatus(TaskStatus.PENDING);

        Task updatedTask = new Task();

        updatedTask.setTitle("Updated Title");
        updatedTask.setDescription("Updated Description");
        updatedTask.setStatus(TaskStatus.COMPLETED);

        when(repository.findById(1L))
                .thenReturn(Optional.of(existingTask));

        when(repository.save(existingTask))
                .thenReturn(existingTask);

        Task result =
                service.updateTask(1L, updatedTask);

        assertEquals(
                "Updated Title",
                result.getTitle()
        );

        assertEquals(
                "Updated Description",
                result.getDescription()
        );

        assertEquals(
                TaskStatus.COMPLETED,
                result.getStatus()
        );

        verify(repository).findById(1L);
        verify(repository).save(existingTask);
    }


    @Test
    void testDeleteTask() {

        Task task = new Task();

        task.setId(1L);
        task.setTitle("Learn Spring");
        task.setDescription("Study Spring Boot");

        when(repository.findById(1L))
                .thenReturn(Optional.of(task));

        service.deleteTask(1L);

        verify(repository).findById(1L);
        verify(repository).delete(task);
    }


    @Test
    void testDeleteTaskNotFound() {

        when(repository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                TaskNotFoundException.class,
                () -> service.deleteTask(99L)
        );

        verify(repository).findById(99L);

        verify(
                repository,
                never()
        ).delete(any(Task.class));
    }
}