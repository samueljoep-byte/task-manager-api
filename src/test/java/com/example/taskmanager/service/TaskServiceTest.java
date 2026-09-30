package com.example.taskmanager.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.junit.jupiter.api.BeforeEach;

import com.example.taskmanager.TaskStatus;
import com.example.taskmanager.dto.TaskRequest;
import com.example.taskmanager.dto.TaskResponse;
import com.example.taskmanager.entity.Task;
import com.example.taskmanager.exception.TaskNotFoundException;
import com.example.taskmanager.mapper.TaskMapper;
import com.example.taskmanager.repository.TaskRepository;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;

import com.example.taskmanager.entity.User;
import com.example.taskmanager.repository.UserRepository;




@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository repository;

    @Mock
    private TaskMapper mapper;

    @InjectMocks
    private TaskService service;
    
    @Mock
    private UserRepository userRepository;


    // CREATE TEST
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


        when(mapper.toEntity(request))
                .thenReturn(task);

        when(repository.save(task))
                .thenReturn(task);

        when(mapper.toResponse(task))
                .thenReturn(response);


        TaskResponse result =
                service.createTask(request);


        assertEquals(
                "Learn Spring",
                result.getTitle()
        );

        verify(mapper).toEntity(request);
        verify(repository).save(task);
        verify(mapper).toResponse(task);
        assertEquals(
                "Learn Spring",
                result.getTitle()
        );

        verify(mapper).toEntity(request);
        verify(repository).save(task);
        verify(mapper).toResponse(task);

        assertEquals(
                30L,
                task.getUser().getId()
        );
    }


    // GET BY ID - NOT FOUND TEST
    @Test
    void testGetTaskByIdNotFound() {

        User user = userRepository
                .findByUsername("samuel2")
                .orElseThrow();

        when(repository.findByIdAndUser(99L, user))
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

        verify(repository).findByIdAndUser(99L, user);
    }


    // UPDATE TEST
    @Test
    void testUpdateTask() {

        Task existingTask = new Task();

        existingTask.setId(1L);
        existingTask.setTitle("Old Title");
        existingTask.setDescription("Old Description");
        existingTask.setStatus(TaskStatus.PENDING);


        TaskRequest request = new TaskRequest();

        request.setTitle("Updated Title");
        request.setDescription("Updated Description");
        request.setStatus(TaskStatus.COMPLETED);


        TaskResponse response = new TaskResponse();

        response.setTitle("Updated Title");
        response.setDescription("Updated Description");
        response.setStatus(TaskStatus.COMPLETED);


        User user = userRepository
                .findByUsername("samuel2")
                .orElseThrow();

        when(repository.findByIdAndUser(1L, user))
                .thenReturn(Optional.of(existingTask));
        
        when(repository.save(existingTask))
                .thenReturn(existingTask);

        when(mapper.toResponse(existingTask))
                .thenReturn(response);


        TaskResponse result =
                service.updateTask(1L, request);


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


        verify(repository).findByIdAndUser(1L, user);

        verify(repository).save(existingTask);

        verify(mapper).toResponse(existingTask);
    }


    // DELETE TEST
    @Test
    void testDeleteTask() {

        Task task = new Task();

        task.setId(1L);
        task.setTitle("Learn Spring");
        task.setDescription("Study Spring Boot");


        User user = userRepository
                .findByUsername("samuel2")
                .orElseThrow();

        when(repository.findByIdAndUser(1L, user))
                .thenReturn(Optional.of(task));

        service.deleteTask(1L);


        verify(repository).findByIdAndUser(1L, user);

        verify(repository).delete(task);
    }


    // DELETE - NOT FOUND TEST
    @Test
    void testDeleteTaskNotFound() {

        User user = userRepository
                .findByUsername("samuel2")
                .orElseThrow();

        when(repository.findByIdAndUser(99L, user))
                .thenReturn(Optional.empty());

        assertThrows(
                TaskNotFoundException.class,
                () -> service.deleteTask(99L)
        );

        verify(repository).findByIdAndUser(99L, user);
        verify(repository, never()).delete(any(Task.class));
    }
    
    @BeforeEach
    void setUpSecurityContext() {

        User user = new User();
        user.setId(30L);
        user.setUsername("samuel2");
        user.setRole("USER");

        when(userRepository.findByUsername("samuel2"))
                .thenReturn(Optional.of(user));

        SecurityContext context =
                SecurityContextHolder.createEmptyContext();

        context.setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        "samuel2",
                        null
                )
        );

        SecurityContextHolder.setContext(context);
    }
}