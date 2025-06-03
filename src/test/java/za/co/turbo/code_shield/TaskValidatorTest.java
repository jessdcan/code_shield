package za.co.turbo.code_shield;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;
import org.springframework.beans.factory.annotation.Autowired;

import za.co.turbo.code_shield.model.Task;
import za.co.turbo.code_shield.model.TaskStatus;
import za.co.turbo.code_shield.validator.TaskValidator;
import za.co.turbo.code_shield.mother.TaskMother;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@Tag("validation")
@Tag("unit")
public class TaskValidatorTest extends BaseTest {
    @Autowired
    private TaskValidator taskValidator;

    private Task validTask;

    @BeforeEach
    void setUp() {
        validTask = TaskMother.validTask();
    }

    @Test
    void validate_ValidTask_DoesNotThrowException() {
        assertDoesNotThrow(() -> taskValidator.validate(validTask),
            "Valid task should not throw any validation exceptions");
    }

    @Test
    void validate_EmptyTitle_ThrowsException() {
        Task invalidTask = TaskMother.invalidTaskWithEmptyTitle();
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> taskValidator.validate(invalidTask),
            "Empty title should throw IllegalArgumentException");
        assertEquals("Task title is required", exception.getMessage());
    }

    @Test
    void validate_NullTitle_ThrowsException() {
        Task invalidTask = TaskMother.taskWithTitle(null);
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> taskValidator.validate(invalidTask),
            "Empty title should throw IllegalArgumentException");
        assertEquals("Task title is required", exception.getMessage());
    }

    @Test
    void validate_NullDueDate_ThrowsException() {
        Task invalidTask = TaskMother.taskWithDueDate(null);
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> taskValidator.validate(invalidTask),
            "Null due date should throw IllegalArgumentException");
        assertEquals("Due date is required", exception.getMessage());
    }

    @Test
    void validate_PastDueDate_ThrowsException() {
        Task invalidTask = TaskMother.invalidTaskWithPastDueDate();
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> taskValidator.validate(invalidTask),
            "Past due date should throw IllegalArgumentException");
        assertEquals("Due date cannot be in the past", exception.getMessage());
    }

    @Test
    void validate_NullStatus_ThrowsException() {
        Task invalidTask = TaskMother.invalidTaskWithNullStatus();
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> taskValidator.validate(invalidTask),
            "Null status should throw IllegalArgumentException");
        assertEquals("Task status is required", exception.getMessage());
    }
}