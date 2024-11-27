package ru.novikov.T1.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.novikov.T1.aspect.LogAfterReturningAspect;
import ru.novikov.T1.aspect.LogAroundAspect;
import ru.novikov.T1.aspect.LogBeforeAspect;
import ru.novikov.T1.aspect.LogExceptionAspect;
import ru.novikov.T1.kafka.KafkaTaskProducer;
import ru.novikov.T1.models.Task;
import ru.novikov.T1.repositories.TaskRepository;
import ru.novikov.T1.util.TaskNotFoundException;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final KafkaTaskProducer kafkaTaskProducer;

    @LogBeforeAspect
    @LogAroundAspect
    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    @LogBeforeAspect
    @LogExceptionAspect
    public Task getTaskById(Long id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException("Task not found"));
    }

    @Transactional
    @LogAroundAspect
    @LogAfterReturningAspect
    @LogExceptionAspect
    public String createTask(Task task) {
        taskRepository.save(task);
        return "Task created";
    }

    @Transactional
    @LogAroundAspect
    @LogAfterReturningAspect
    @LogExceptionAspect
    public String updateTask(Long id, Task taskDetails) {
        Task task = getTaskById(id);
        task.setTitle(taskDetails.getTitle());
        task.setDescription(taskDetails.getDescription());
        task.setUserId(taskDetails.getUserId());

        if (!task.getStatus().equals(taskDetails.getStatus())) {
            task.setStatus(taskDetails.getStatus());
            kafkaTaskProducer.sendTaskUpdate(id, taskDetails.getStatus());
        }

        taskRepository.save(task);
        return "Task updated";
    }

    @Transactional
    @LogAroundAspect
    @LogAfterReturningAspect
    @LogExceptionAspect
    public void deleteTask(Long id) {
        Task task = getTaskById(id);
        taskRepository.delete(task);
    }
}
