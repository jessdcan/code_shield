package za.co.turbo.code_shield.builder;

import za.co.turbo.code_shield.model.Task;
import za.co.turbo.code_shield.model.TaskStatus;
import za.co.turbo.code_shield.model.User;

import java.time.LocalDateTime;

public class TaskTestDataBuilder {
    private String title = "Default Task";
    private String description = "Default Description";
    private TaskStatus status = TaskStatus.TODO;
    private LocalDateTime dueDate = LocalDateTime.now().plusDays(1);
    private User assignee;

    public TaskTestDataBuilder withTitle(String title) {
        this.title = title;
        return this;
    }

    public TaskTestDataBuilder withDescription(String description) {
        this.description = description;
        return this;
    }

    public TaskTestDataBuilder withStatus(TaskStatus status) {
        this.status = status;
        return this;
    }

    public TaskTestDataBuilder withDueDate(LocalDateTime dueDate) {
        this.dueDate = dueDate;
        return this;
    }

    public TaskTestDataBuilder withAssignee(User assignee) {
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

    public static TaskTestDataBuilder aTask() {
        return new TaskTestDataBuilder();
    }
} 