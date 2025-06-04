package za.co.turbo.code_shield.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import za.co.turbo.code_shield.model.Task;
import za.co.turbo.code_shield.model.TaskStatus;
import za.co.turbo.code_shield.model.User;
import za.co.turbo.code_shield.mother.TaskMother;
import za.co.turbo.code_shield.service.TaskService;
import za.co.turbo.code_shield.validator.TaskValidator;
import za.co.turbo.code_shield.exception.EntityNotFoundException;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@WebMvcTest(TaskController.class)
@Tag("controller")
@Tag("unit")
public class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TaskValidator taskValidator;

    @MockBean
    private TaskService taskService;

    private Task testTask;
    private User testUser;

    private TaskMother taskMother = new TaskMother();

    @BeforeEach
    void setUp() {
        // Create test user
        testUser = User.builder()
                .id(1L)
                .username("testuser")
                .email("test@test.com")
                .build();

        // Create test task
        testTask = Task.builder()
                .id(1L)
                .title("Test Task")
                .description("Test Description")
                .status(TaskStatus.TODO)
                .dueDate(LocalDateTime.now().plusDays(1))
                .assignee(testUser)
                .build();
    }

    @Test
    void getTask_ExistingTask_ReturnsTask() throws Exception {
        when(taskService.getTask(1L)).thenReturn(testTask);

        mockMvc.perform(get("/api/tasks/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Test Task"))
                .andExpect(jsonPath("$.status").value("TODO"));

        verify(taskService, times(1)).getTask(1L);
    }

    @Test
    void getAllTasks_ReturnsTaskList() throws Exception {
        List<Task> tasks = Arrays.asList(testTask);
        when(taskService.getAllTasks()).thenReturn(tasks);

        mockMvc.perform(get("/api/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].title").value("Test Task"));

        verify(taskService, times(1)).getAllTasks();
    }

    @Test
    void createTask_ValidTask_ReturnsCreatedTask() throws Exception {
        when(taskService.createTask(any(Task.class))).thenReturn(testTask);

        mockMvc.perform(post("/api/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testTask)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Test Task"));

        verify(taskService, times(1)).createTask(any(Task.class));
    }

    @Test
    void updateTask_ExistingTask_ReturnsUpdatedTask() throws Exception {
        Task updatedTask = Task.builder()
                .id(1L)
                .title("Updated Task")
                .description("Updated Description")
                .status(TaskStatus.IN_PROGRESS)
                .dueDate(LocalDateTime.now().plusDays(1))
                .assignee(testUser)
                .build();

        when(taskService.updateTask(eq(1L), any(Task.class))).thenReturn(updatedTask);

        mockMvc.perform(put("/api/tasks/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedTask)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated Task"))
                .andExpect(jsonPath("$.status").value("IN_PROGRESS"));

        verify(taskService, times(1)).updateTask(eq(1L), any(Task.class));
    }

    @Test
    void deleteTask_ExistingTask_ReturnsNoContent() throws Exception {
        doNothing().when(taskService).deleteTask(1L);

        mockMvc.perform(delete("/api/tasks/1"))
                .andExpect(status().isNoContent());

        verify(taskService, times(1)).deleteTask(1L);
    }

    @Test
    void getTask_NonExistingTask_ReturnsNotFound() throws Exception {
        when(taskService.getTask(999L)).thenThrow(new EntityNotFoundException("Task", 999L));

        mockMvc.perform(get("/api/tasks/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Task with id 999 not found"))
                .andExpect(jsonPath("$.timestamp").exists());

        verify(taskService, times(1)).getTask(999L);
    }

    @Test
    void createTask_InvalidTask_ReturnsBadRequest() throws Exception {
        Task invalidTask = Task.builder()
                .title("") // Empty title should fail validation
                .build();

        when(taskService.createTask(any(Task.class)))
            .thenThrow(new IllegalArgumentException("Task title is required"));

        mockMvc.perform(post("/api/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidTask)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.errors.message").value("Task title is required"));

        verify(taskService, times(1)).createTask(any(Task.class));
    }
} 