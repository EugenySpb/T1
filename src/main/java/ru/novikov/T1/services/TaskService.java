package ru.novikov.T1.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.novikov.T1.aspect.LogAfterReturningAspect;
import ru.novikov.T1.aspect.LogAroundAspect;
import ru.novikov.T1.aspect.LogBeforeAspect;
import ru.novikov.T1.aspect.LogExceptionAspect;
import ru.novikov.T1.models.Task;
import ru.novikov.T1.repositories.TaskRepository;
import ru.novikov.T1.util.TaskNotFoundException;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class TaskService {

    private final TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @LogBeforeAspect
    @LogAroundAspect
    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    @LogBeforeAspect
    @LogExceptionAspect
    public Task getTaskById(Long id) {
        return taskRepository.findById(id).orElseThrow(() -> new TaskNotFoundException("Task not found"));
    }

    @Transactional
    @LogAroundAspect
    @LogAfterReturningAspect
    @LogExceptionAspect
    public void createTask(Task task) {
        try {
            taskRepository.save(task);
        } catch (Exception e) {
            throw new RuntimeException("Error saving task: " + e.getMessage(), e);
        }
    }

    @Transactional
    @LogAroundAspect
    @LogAfterReturningAspect
    @LogExceptionAspect
    public void updateTask(Long id, Task taskDetails) {
        Task task = getTaskById(id);
        try {
            task.setTitle(taskDetails.getTitle());
            task.setDescription(taskDetails.getDescription());
            task.setUserId(taskDetails.getUserId());
            taskRepository.save(task);
        } catch (Exception e) {
            throw new RuntimeException("Error updating task: " + e.getMessage(), e);
        }
    }

    @Transactional
    @LogAroundAspect
    @LogAfterReturningAspect
    @LogExceptionAspect
    public void deleteTask(Long id) {
        Task task = getTaskById(id);
        try {
            taskRepository.delete(task);
        } catch (Exception e) {
            throw new RuntimeException("Error deleting task: " + e.getMessage(), e);
        }
    }
}
