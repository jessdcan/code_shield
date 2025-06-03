package za.co.turbo.code_shield.builder;

import za.co.turbo.code_shield.model.Task;
import za.co.turbo.code_shield.model.TaskStatus;
import za.co.turbo.code_shield.model.User;

import java.time.LocalDateTime;

public class TaskBuilder {
    private String title = "Default Task";
    private String description = "Default Description";
    private TaskStatus status = TaskStatus.TODO;
    private LocalDateTime dueDate = LocalDateTime.now().plusDays(1);
    private User assignee;

    public TaskBuilder withTitle(String title) {
        this.title = title;
        return this;
    }

    public TaskBuilder withDescription(String description) {
        this.description = description;
        return this;
    }

    public TaskBuilder withStatus(TaskStatus status) {
        this.status = status;
        return this;
    }

    public TaskBuilder withDueDate(LocalDateTime dueDate) {
        this.dueDate = dueDate;
        return this;
    }

    public TaskBuilder withAssignee(User assignee) {
        this.assignee = assignee;
        return this;
    }

    public Task build() {
        return Task.builder()
                .title(title)
                .description(description)
                .status(status)
                .dueDate(dueDate)
                .assignee(assignee)
                .build();
    }

    public static TaskBuilder aTask() {
        return new TaskBuilder();
    }
} 