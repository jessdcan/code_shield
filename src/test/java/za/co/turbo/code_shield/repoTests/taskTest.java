package za.co.turbo.code_shield.repoTests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import za.co.turbo.code_shield.BaseTest;
import za.co.turbo.code_shield.model.Task;
import za.co.turbo.code_shield.model.TaskStatus;
import za.co.turbo.code_shield.model.User;
import za.co.turbo.code_shield.repository.TaskRepository;

public class taskTest extends BaseTest {

    @Autowired
    private TaskRepository taskRepository;
    
    @Test
    public void taskModelTest() {
        Task actual = new Task();
        actual.setTitle("Test Task");
        actual.setDescription("Test Description");
        actual.setStatus(TaskStatus.TODO);
        actual.setDueDate(LocalDateTime.now());
        actual.setCreatedAt(LocalDateTime.now());
        actual.setAssignee(new User());

        // Task actual = taskRepository.save(expected);

        // assertEquals(expected.getId(), actual.getId());

        // assertNotNull(task.getId());
        assertNotNull(actual.getTitle());
        assertNotNull(actual.getDescription());
        assertNotNull(actual.getStatus());
        assertNotNull(actual.getDueDate());
        assertNotNull(actual.getCreatedAt());
        assertNotNull(actual.getAssignee());
    }
}
// test save works
// Flyway setup her for repository tests
// create a springboot application class for test "active profile test"

// for other tests, mock out the repository
// call the service and assert what is returned
