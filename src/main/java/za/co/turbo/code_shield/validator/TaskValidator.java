package za.co.turbo.code_shield.validator;

import org.springframework.stereotype.Component;
import za.co.turbo.code_shield.model.Task;

import java.time.LocalDateTime;

@Component
public class TaskValidator {
    public void validate(Task task) {
        if (task.getTitle() == null || task.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("Task title is required");
        }

        if (task.getDueDate() == null) {
            throw new IllegalArgumentException("Due date is required");
        } else if (task.getDueDate().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Due date cannot be in the past");
        }

        if (task.getStatus() == null) {
            throw new IllegalArgumentException("Task status is required");
        }
    }
}
