package za.co.turbo.code_shield.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.jdbc.Sql;
import za.co.turbo.code_shield.model.Task;
import za.co.turbo.code_shield.model.TaskStatus;
import za.co.turbo.code_shield.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Sql("/sql/insert_overdue_tasks.sql")
class TaskRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserRepository userRepository;

    private User testUser;
    private Task testTask;

    @BeforeEach
    void setUp() {
        // Retrieve the user that was inserted by the SQL script
        testUser = userRepository.findAll().get(0);
        
        // Retrieve the task that was inserted by the SQL script
        testTask = taskRepository.findAll().get(0);
    }

    @Test
    void whenFindByAssigneeId_thenReturnTasks() {
        // when
        List<Task> found = taskRepository.findByAssigneeId(testUser.getId());

        // then
        assertThat(found).hasSize(3);
        assertThat(found)
            .extracting(Task::getTitle)
            .containsExactlyInAnyOrder("Overdue Task 1", "Overdue Task 2", "Completed Task");
    }

    @Test
    void whenFindByStatus_thenReturnTasks() {
        // when
        List<Task> found = taskRepository.findByStatus(testTask.getStatus());

        // then
        assertThat(found).hasSize(1);
        assertThat(found.get(0).getStatus()).isEqualTo(testTask.getStatus());
    }

    @Test
    void whenFindOverdueTasks_thenReturnTasks() {
        // when
        List<Task> found = taskRepository.findOverdueTasks(LocalDateTime.now());

        // then
        assertThat(found).isNotEmpty();
        assertThat(found.get(0).getStatus()).isNotEqualTo(TaskStatus.COMPLETED);
    }

    @Test
    void whenSaveTask_thenPersistTask() {
        // given
        Task newTask = Task.builder()
                .title("New Task")
                .description("New Description")
                .status(TaskStatus.TODO)
                .dueDate(LocalDateTime.now().plusDays(1))
                .assignee(testUser)
                .build();

        // when
        Task saved = taskRepository.save(newTask);

        // then
        assertThat(saved.getId()).isNotNull();
        assertThat(entityManager.find(Task.class, saved.getId())).isNotNull();
    }

    @Test
    void whenDeleteTask_thenRemoveTask() {
        // when
        taskRepository.deleteById(testTask.getId());

        // then
        assertThat(entityManager.find(Task.class, testTask.getId())).isNull();
    }
} 