package ru.novikov.T1.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.novikov.T1.dto.TaskDTO;
import ru.novikov.T1.kafka.KafkaTaskProducer;
import ru.novikov.T1.models.Task;
import ru.novikov.T1.models.TaskStatus;
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
    public List<TaskDTO> getAllTasks() {
        return taskRepository.findAll().stream()
                .map(taskMapper::toDto)
                .toList();
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
    public String createTask(TaskDTO taskDTO) {
        Task task = taskMapper.toEntity(taskDTO);
        taskRepository.save(task);
        return "Task created";
    }

    @Transactional
    @LogAroundAspect
    @LogAfterReturningAspect
    @LogExceptionAspect
    public String updateTask(Long id, TaskDTO taskDTO) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException("Task not found"));

        task.setTitle(taskDTO.getTitle());
        task.setDescription(taskDTO.getDescription());
        task.setUserId(taskDTO.getUserId());

        boolean statusUpdated = !task.getStatus().name().equals(taskDTO.getStatus());
        if (statusUpdated) {
            task.setStatus(TaskStatus.valueOf(taskDTO.getStatus()));
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
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException("Task not found"));
        taskRepository.delete(task);
    }
}
