package za.co.turbo.code_shield;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import za.co.turbo.code_shield.model.Task;
import za.co.turbo.code_shield.model.TaskStatus;
import za.co.turbo.code_shield.model.User;
import za.co.turbo.code_shield.repository.TaskRepository;
import za.co.turbo.code_shield.repository.UserRepository;

public class PersistanceTest extends BaseTest {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserRepository userRepository;

    private User testUser;

    @BeforeEach
    public void setUp() {
        testUser = User.builder()
            .username("testuser")
            .email("test@test.com")
            .password("testpassword")
            .build();
        userRepository.save(testUser);
    }

    @Test
    public void taskModelTest() {
        Task expected = new Task();
        expected.setTitle("Test Task");
        expected.setDescription("Test Description");
        expected.setStatus(TaskStatus.TODO);
        expected.setDueDate(LocalDateTime.now());
        expected.setCreatedAt(LocalDateTime.now());
        expected.setAssignee(testUser);

        Task actual = taskRepository.save(expected);

        assertEquals(expected.getId(), actual.getId());

        assertNotNull(actual.getId());
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
