package za.co.turbo.code_shield.mother;

import za.co.turbo.code_shield.model.Task;
import za.co.turbo.code_shield.model.TaskStatus;
import za.co.turbo.code_shield.model.User;

import java.time.LocalDateTime;

/**
 * Object Mother for creating test Task objects.
 * Provides factory methods to create tasks in different states for testing.
 */
public class TaskMother {

    /**
     * Creates a valid task with default values
     */
    public static Task validTask() {
        return Task.builder()
                .title("Valid Task")
                .description("A valid task for testing")
                .status(TaskStatus.TODO)
                .dueDate(LocalDateTime.now().plusDays(1))
                .build();
    }

    /**
     * Creates a task with a specific title
     */
    public static Task taskWithTitle(String title) {
        return Task.builder()
                .title(title)
                .description("Task with specific title")
                .status(TaskStatus.TODO)
                .dueDate(LocalDateTime.now().plusDays(1))
                .build();
    }

    /**
     * Creates a task with a specific status
     */
    public static Task taskWithStatus(TaskStatus status) {
        return Task.builder()
                .title("Task with specific status")
                .description("Task description")
                .status(status)
                .dueDate(LocalDateTime.now().plusDays(1))
                .build();
    }

    /**
     * Creates a task with a specific due date
     */
    public static Task taskWithDueDate(LocalDateTime dueDate) {
        return Task.builder()
                .title("Task with specific due date")
                .description("Task description")
                .status(TaskStatus.TODO)
                .dueDate(dueDate)
                .build();
    }

    /**
     * Creates a task with a specific assignee
     */
    public static Task taskWithAssignee(User assignee) {
        return Task.builder()
                .title("Task with assignee")
                .description("Task description")
                .status(TaskStatus.TODO)
                .dueDate(LocalDateTime.now().plusDays(1))
                .assignee(assignee)
                .build();
    }

    /**
     * Creates an invalid task with empty title
     */
    public static Task invalidTaskWithEmptyTitle() {
        return Task.builder()
                .title("")
                .description("Invalid task with empty title")
                .status(TaskStatus.TODO)
                .dueDate(LocalDateTime.now().plusDays(1))
                .build();
    }

    /**
     * Creates an invalid task with past due date
     */
    public static Task invalidTaskWithPastDueDate() {
        return Task.builder()
                .title("Invalid task")
                .description("Task with past due date")
                .status(TaskStatus.TODO)
                .dueDate(LocalDateTime.now().minusDays(1))
                .build();
    }

    /**
     * Creates an invalid task with null status
     */
    public static Task invalidTaskWithNullStatus() {
        return Task.builder()
                .title("Invalid task")
                .description("Task with null status")
                .dueDate(LocalDateTime.now().plusDays(1))
                .build();
    }
} 