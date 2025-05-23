package za.co.turbo.code_shield.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import za.co.turbo.code_shield.model.Task;
import za.co.turbo.code_shield.model.TaskStatus;

import java.time.LocalDateTime;
import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByAssigneeId(Long userId);

    List<Task> findByStatus(TaskStatus status);

    @Query("SELECT t FROM Task t WHERE t.dueDate < :date AND t.status != 'COMPLETED'")
    List<Task> findOverdueTasks(@Param("date") LocalDateTime date);
}
