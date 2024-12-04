package ru.novikov.T1.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.novikov.T1.dto.TaskDTO;
import ru.novikov.T1.kafka.KafkaTaskProducer;
import ru.novikov.T1.models.Task;
import ru.novikov.T1.repositories.TaskRepository;
import ru.novikov.T1.util.TaskMapper;
import ru.novikov.T1.util.TaskNotFoundException;
import ru.novikov.startert1.aspect.LogAfterReturningAspect;
import ru.novikov.startert1.aspect.LogAroundAspect;
import ru.novikov.startert1.aspect.LogBeforeAspect;
import ru.novikov.startert1.aspect.LogExceptionAspect;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final KafkaTaskProducer kafkaTaskProducer;
    private final TaskMapper taskMapper;

    @LogBeforeAspect
    @LogAroundAspect
    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    @LogBeforeAspect
    @LogExceptionAspect
    public TaskDTO getTaskById(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException("Task not found"));
        return taskMapper.toDto(task);
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
        TaskDTO taskDto = getTaskById(id);
        Task task = taskMapper.toEntity(taskDto);

        task.setTitle(taskDetails.getTitle());
        task.setDescription(taskDetails.getDescription());
        task.setUserId(taskDetails.getUserId());

        boolean statusUpdated = !task.getStatus().equals(taskDetails.getStatus());
        if (statusUpdated) {
            task.setStatus(taskDetails.getStatus());
        }

        taskRepository.save(task);

        if (statusUpdated) {
            kafkaTaskProducer.sendTaskUpdate(id, task.getStatus());
        }
        return "Task updated";
    }

    @Transactional
    @LogAroundAspect
    @LogAfterReturningAspect
    @LogExceptionAspect
    public void deleteTask(Long id) {
        TaskDTO taskDto = getTaskById(id);
        Task task = taskMapper.toEntity(taskDto);
        taskRepository.delete(task);
    }
}
