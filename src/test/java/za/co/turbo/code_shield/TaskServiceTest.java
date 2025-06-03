package za.co.turbo.code_shield;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.Tag;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import za.co.turbo.code_shield.model.Task;
import za.co.turbo.code_shield.model.TaskStatus;
import za.co.turbo.code_shield.model.User;
import za.co.turbo.code_shield.repository.TaskRepository;
import za.co.turbo.code_shield.repository.UserRepository;
import za.co.turbo.code_shield.service.TaskService;
import za.co.turbo.code_shield.exception.EntityNotFoundException;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Tag("service")
@Tag("unit")
public class TaskServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private TaskService taskService;

    @Captor
    private ArgumentCaptor<Task> taskCaptor;

    private Task testTask;
    private User testUser;
    private static final Long TEST_USER_ID = 1L;
    private static final Long TEST_TASK_ID = 1L;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(TEST_USER_ID)
                .username("testuser")
                .email("test@test.com")
                .password("testpassword")
                .build();

        testTask = Task.builder()
                .id(TEST_TASK_ID)
                .title("Test Task")
                .description("Test Description")
                .status(TaskStatus.TODO)
                .dueDate(LocalDateTime.now().plusDays(1))
                .assignee(testUser)
                .build();
    }

    @Test
    void getTask_ExistingTask_ReturnsTask() {
        when(taskRepository.findById(TEST_TASK_ID)).thenReturn(Optional.of(testTask));

        Task result = taskService.getTask(TEST_TASK_ID);

        assertNotNull(result);
        assertEquals(testTask.getId(), result.getId());
        assertEquals(testTask.getTitle(), result.getTitle());
        verify(taskRepository, times(1)).findById(TEST_TASK_ID);
    }

    @Test
    void getAllTasks_ReturnsTaskList() {
        List<Task> tasks = Arrays.asList(testTask);
        when(taskRepository.findAll()).thenReturn(tasks);

        List<Task> result = taskService.getAllTasks();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testTask.getId(), result.get(0).getId());
        verify(taskRepository, times(1)).findAll();
    }

    @Test
    void createTask_ValidTask_ReturnsCreatedTask() {
        Task newTask = Task.builder()
                .title("New Task")
                .description("New Description")
                .status(TaskStatus.TODO)
                .dueDate(LocalDateTime.now().plusDays(1))
                .assignee(testUser)
                .build();

        when(taskRepository.save(any(Task.class))).thenReturn(newTask);

        Task result = taskService.createTask(newTask);

        assertNotNull(result);
        assertEquals("New Task", result.getTitle());
        verify(taskRepository, times(1)).save(any(Task.class));
    }

    @Test
    void updateTask_ExistingTask_ReturnsUpdatedTask() {
        Task updatedTask = Task.builder()
                .id(TEST_TASK_ID)
                .title("Updated Task")
                .status(TaskStatus.IN_PROGRESS)
                .build();
        
        when(taskRepository.save(any(Task.class))).thenReturn(updatedTask);
        when(taskRepository.existsById(TEST_TASK_ID)).thenReturn(true);

        Task result = taskService.updateTask(TEST_TASK_ID, updatedTask);

        assertNotNull(result);
        assertEquals("Updated Task", result.getTitle());
        assertEquals(TaskStatus.IN_PROGRESS, result.getStatus());
        verify(taskRepository, times(1)).existsById(TEST_TASK_ID);
        verify(taskRepository, times(1)).save(any(Task.class));
    }

    @Test
    void deleteTask_ExistingTask_DeletesTask() {
        doNothing().when(taskRepository).deleteById(TEST_TASK_ID);

        taskService.deleteTask(TEST_TASK_ID);

        verify(taskRepository, times(1)).deleteById(TEST_TASK_ID);
    }

    @Test
    void getTask_NonExistingTask_ThrowsException() {
        when(taskRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> taskService.getTask(999L));
        verify(taskRepository, times(1)).findById(999L);
    }

    @Test
    void createTask_WithSpy_VerifiesRepositoryInteraction() {
        // Create a spy of the TaskService
        TaskService spyTaskService = spy(taskService);
        
        Task newTask = Task.builder()
                .title("Spy Test Task")
                .description("Testing with Spy")
                .status(TaskStatus.TODO)
                .dueDate(LocalDateTime.now().plusDays(1))
                .assignee(testUser)
                .build();

        when(taskRepository.save(any(Task.class))).thenReturn(newTask);

        Task result = spyTaskService.createTask(newTask);

        assertNotNull(result);
        assertEquals("Spy Test Task", result.getTitle());
        
        verify(spyTaskService, times(1)).createTask(newTask);
        
        verify(taskRepository, times(1)).save(any(Task.class));
    }

    @Test
    void getTask_NonExistingTask_ThrowsEntityNotFoundException() {
        Long nonExistentTaskId = 999L;
        doThrow(new EntityNotFoundException("Task", nonExistentTaskId))
            .when(taskRepository).findById(nonExistentTaskId);

        EntityNotFoundException exception = assertThrows(
            EntityNotFoundException.class,
            () -> taskService.getTask(nonExistentTaskId)
        );

        assertEquals("Task with id 999 not found", exception.getMessage());
        verify(taskRepository, times(1)).findById(nonExistentTaskId);
    }

    @Test
    void getTask_UpdateNonExistingTask_ThrowsEntityNotFoundException() {
        Task updatedNonExistingTask = Task.builder()
                .id(999L)
                .title("Updated Non Existing Task")
                .status(TaskStatus.IN_PROGRESS)
                .build();
        when(taskRepository.existsById(999L)).thenReturn(false);

        EntityNotFoundException exception = assertThrows(
            EntityNotFoundException.class,
            () -> taskService.updateTask(999L, updatedNonExistingTask)
        );

        assertEquals("Task with id 999 not found", exception.getMessage());
        verify(taskRepository, times(1)).existsById(999L);
    }

    @Test
    void createTask_WithArgumentMatchers_VerifiesTaskCreation() {
        Task newTask = Task.builder()
                .title("New Task")
                .description("New Description")
                .status(TaskStatus.TODO)
                .dueDate(LocalDateTime.now().plusDays(1))
                .assignee(testUser)
                .build();

        when(taskRepository.save(argThat(task -> 
            task.getTitle().equals("New Task") && 
            task.getStatus() == TaskStatus.TODO
        ))).thenReturn(newTask);

        Task result = taskService.createTask(newTask);

        assertNotNull(result);
        assertEquals("New Task", result.getTitle());
        verify(taskRepository).save(argThat(task -> 
            task.getTitle().equals("New Task") && 
            task.getStatus() == TaskStatus.TODO
        ));
    }

    @Test
    void updateTask_WithArgumentCaptor_VerifiesTaskUpdates() {
        Task updatedTask = Task.builder()
                .id(TEST_TASK_ID)
                .title("Updated Task")
                .description("Updated Description")
                .status(TaskStatus.IN_PROGRESS)
                .dueDate(LocalDateTime.now().plusDays(2))
                .assignee(testUser)
                .build();
        
        when(taskRepository.existsById(TEST_TASK_ID)).thenReturn(true);
        when(taskRepository.save(any(Task.class))).thenReturn(updatedTask);

        Task result = taskService.updateTask(TEST_TASK_ID, updatedTask);

        verify(taskRepository).save(taskCaptor.capture());
        Task capturedTask = taskCaptor.getValue();

        assertNotNull(result);
        assertEquals(TEST_TASK_ID, capturedTask.getId());
        assertEquals("Updated Task", capturedTask.getTitle());
        assertEquals("Updated Description", capturedTask.getDescription());
        assertEquals(TaskStatus.IN_PROGRESS, capturedTask.getStatus());
        assertNotNull(capturedTask.getDueDate());
        assertEquals(testUser, capturedTask.getAssignee());
    }

    @Test
    void createTask_WithComplexArgumentMatchers_VerifiesTaskCreation() {
        Task newTask = Task.builder()
                .title("Complex Task")
                .description("Complex Description")
                .status(TaskStatus.TODO)
                .dueDate(LocalDateTime.now().plusDays(1))
                .assignee(testUser)
                .build();

        when(taskRepository.save(argThat(task -> 
            task.getTitle().contains("Complex") && 
            task.getDescription().length() > 10 &&
            task.getStatus() == TaskStatus.TODO &&
            task.getDueDate().isAfter(LocalDateTime.now())
        ))).thenReturn(newTask);

        Task result = taskService.createTask(newTask);

        assertNotNull(result);
        verify(taskRepository).save(argThat(task -> 
            task.getTitle().contains("Complex") && 
            task.getDescription().length() > 10 &&
            task.getStatus() == TaskStatus.TODO &&
            task.getDueDate().isAfter(LocalDateTime.now())
        ));
    }
}

//@spy on service 