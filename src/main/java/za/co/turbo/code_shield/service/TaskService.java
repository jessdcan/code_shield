package za.co.turbo.code_shield.service;


import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import za.co.turbo.code_shield.exception.EntityNotFoundException;
import za.co.turbo.code_shield.model.Task;
import za.co.turbo.code_shield.repository.TaskRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskService {
    @Autowired
    private final TaskRepository taskRepository;

    @Cacheable(value = "tasks", key = "#id")
    public Task getTask(Long id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Task", id));
    }

    @CacheEvict(value = "tasks", key = "#result.id")
    public Task createTask(Task task) {
        return taskRepository.save(task);
    }

    @CacheEvict(value = "tasks", key = "#id")
    public Task updateTask(Long id, Task task) {
        if (!taskRepository.existsById(id)) {
            throw new EntityNotFoundException("Task", id);
        }
        task.setId(id);
        return taskRepository.save(task);
    }

    @CacheEvict(value = "tasks", key = "#id")
    public void deleteTask(Long id) {
        taskRepository.deleteById(id);
    }

    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }
}
