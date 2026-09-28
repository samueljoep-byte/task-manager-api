package com.example.taskmanager.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.taskmanager.dto.TaskRequest;
import com.example.taskmanager.dto.TaskResponse;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import com.example.taskmanager.exception.TaskNotFoundException;

import com.example.taskmanager.service.TaskService;

import static org.mockito.Mockito.doNothing;

import com.example.taskmanager.entity.Task;



import com.example.taskmanager.TaskStatus;
import java.util.List;

import org.mockito.Mockito;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import static org.mockito.ArgumentMatchers.any;

import org.springframework.http.MediaType;

import org.mockito.Mock;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@WebMvcTest(TaskController.class)
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TaskService service;

    @Test
    void testGetTasks() throws Exception {

        Page<Task> page = new PageImpl<>(List.of());

        when(service.getTasks(Mockito.any(Pageable.class)))
        .thenReturn(page);

        mockMvc.perform(
                org.springframework.test.web.servlet.request.MockMvcRequestBuilders
                        .get("/tasks")
        )
        .andExpect(
                org.springframework.test.web.servlet.result.MockMvcResultMatchers
                        .status()
                        .isOk()
        )
        .andExpect(
        	    org.springframework.test.web.servlet.result.MockMvcResultMatchers
        	        .jsonPath("$.message")
        	        .value("Tasks retrieved successfully")
        	);
    }
    
    @Test
    void testCreateTask() throws Exception {

        TaskResponse response = new TaskResponse();

        response.setId(1L);
        response.setTitle("Learn Spring");
        response.setDescription("Study Spring Boot");
        response.setStatus(TaskStatus.PENDING);

        when(service.createTask(any(TaskRequest.class)))
                .thenReturn(response);

        mockMvc.perform(
                MockMvcRequestBuilders
                        .post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "title": "Learn Spring",
                                    "description": "Study Spring Boot",
                                    "status": "PENDING"
                                }
                                """)
        )
        .andExpect(
                MockMvcResultMatchers.status()
                        .isCreated()
        )
        .andExpect(
                MockMvcResultMatchers.jsonPath("$.message")
                        .value("Task created successfully")
        )
        .andExpect(
                MockMvcResultMatchers.jsonPath("$.data.title")
                        .value("Learn Spring")
        );
    }
    @Test
    void testCreateTaskValidationError() throws Exception {

        mockMvc.perform(
                org.springframework.test.web.servlet.request.MockMvcRequestBuilders
                        .post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "title": "",
                                    "description": "Study Spring Boot",
                                    "status": "PENDING"
                                }
                                """)
        )
        .andExpect(
                org.springframework.test.web.servlet.result.MockMvcResultMatchers
                        .status()
                        .isBadRequest()
        );
    }
    @Test
    void testGetTaskById() throws Exception {

        Task task = new Task();

        task.setId(1L);
        task.setTitle("Learn Spring");
        task.setDescription("Study Spring Boot");
        task.setStatus(TaskStatus.PENDING);

        when(service.getTaskById(1L))
                .thenReturn(task);

        mockMvc.perform(
                org.springframework.test.web.servlet.request.MockMvcRequestBuilders
                        .get("/tasks/1")
        )
        .andExpect(
                org.springframework.test.web.servlet.result.MockMvcResultMatchers
                        .status()
                        .isOk()
        )
        .andExpect(
                org.springframework.test.web.servlet.result.MockMvcResultMatchers
                        .jsonPath("$.message")
                        .value("Task retrieved successfully")
        )
        .andExpect(
                org.springframework.test.web.servlet.result.MockMvcResultMatchers
                        .jsonPath("$.data.id")
                        .value(1)
        );
    }
    @Test
    void testSearchTasks() throws Exception {

        Task task = new Task();

        task.setId(1L);
        task.setTitle("Learn Spring");
        task.setDescription("Study Spring Boot");
        task.setStatus(TaskStatus.PENDING);

        when(service.searchTasks("Spring"))
                .thenReturn(List.of(task));

        mockMvc.perform(
                MockMvcRequestBuilders
                        .get("/tasks/search")
                        .param("title", "Spring")
        )
        .andExpect(
                MockMvcResultMatchers.status().isOk()
        )
        .andExpect(
                MockMvcResultMatchers.jsonPath("$.message")
                        .value("Tasks found successfully")
        )
        .andExpect(
                MockMvcResultMatchers.jsonPath("$.data[0].title")
                        .value("Learn Spring")
        );
    }
    @Test
    void testGetTasksByStatus() throws Exception {

        Task task = new Task();

        task.setId(1L);
        task.setTitle("Learn Spring");
        task.setDescription("Study Spring Boot");
        task.setStatus(TaskStatus.PENDING);

        when(service.getTasksByStatus(TaskStatus.PENDING))
                .thenReturn(List.of(task));

        mockMvc.perform(
                MockMvcRequestBuilders
                        .get("/tasks/status/PENDING")
        )
        .andExpect(
                MockMvcResultMatchers.status().isOk()
        )
        .andExpect(
                MockMvcResultMatchers.jsonPath("$.message")
                        .value("Tasks filtered by status successfully")
        )
        .andExpect(
                MockMvcResultMatchers.jsonPath("$.data[0].status")
                        .value("PENDING")
        );
    }
    @Test
    void testUpdateTask() throws Exception {

        Task task = new Task();

        task.setId(1L);
        task.setTitle("Updated Spring");
        task.setDescription("Updated description");
        task.setStatus(TaskStatus.IN_PROGRESS);

        when(service.updateTask(any(Long.class), any(Task.class)))
                .thenReturn(task);

        mockMvc.perform(
                MockMvcRequestBuilders
                        .put("/tasks/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "title": "Updated Spring",
                                    "description": "Updated description",
                                    "status": "IN_PROGRESS"
                                }
                                """)
        )
        .andExpect(
                MockMvcResultMatchers.status().isOk()
        )
        .andExpect(
                MockMvcResultMatchers.jsonPath("$.message")
                        .value("Task updated successfully")
        )
        .andExpect(
                MockMvcResultMatchers.jsonPath("$.data.title")
                        .value("Updated Spring")
        );
    }
    
    @Test
    void testDeleteTask() throws Exception {

        doNothing().when(service).deleteTask(1L);

        mockMvc.perform(
                MockMvcRequestBuilders
                        .delete("/tasks/1")
        )
        .andExpect(
                MockMvcResultMatchers.status().isNoContent()
        );
    }
    @Test
    void testGetTaskByIdNotFound() throws Exception {

        when(service.getTaskById(999L))
                .thenThrow(
                        new TaskNotFoundException(
                                "Task not found with id: 999"
                        )
                );

        mockMvc.perform(
                MockMvcRequestBuilders
                        .get("/tasks/999")
        )
        .andExpect(
                MockMvcResultMatchers.status().isNotFound()
        )
        .andExpect(
                MockMvcResultMatchers.jsonPath("$.message")
                        .value("Task not found with id: 999")
        );
    }
  
    @Test
    void testTaskNotFound() throws Exception {

        when(service.getTaskById(999L))
                .thenThrow(new TaskNotFoundException(
                        "Task not found with id: 999"
                ));

        mockMvc.perform(get("/tasks/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message")
                        .value("Task not found with id: 999"));
    }
    @Test
    void testValidationError() throws Exception {

        mockMvc.perform(post("/tasks")
                .contentType("application/json")
                .content("""
                        {
                            "title": "",
                            "description": "Test description",
                            "status": "PENDING"
                        }
                        """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message")
                        .value("Validation failed"))
                .andExpect(jsonPath("$.errors.title")
                        .value("Title is required"));
    }
    @Test
    void testInvalidStatus() throws Exception {

        mockMvc.perform(post("/tasks")
                .contentType("application/json")
                .content("""
                        {
                            "title": "Test Task",
                            "description": "Test Description",
                            "status": "INVALID"
                        }
                        """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message")
                        .value("Invalid task status"))
                .andExpect(jsonPath("$.allowedValues[0]")
                        .value("PENDING"))
                .andExpect(jsonPath("$.allowedValues[1]")
                        .value("IN_PROGRESS"))
                .andExpect(jsonPath("$.allowedValues[2]")
                        .value("COMPLETED"));
    }
}