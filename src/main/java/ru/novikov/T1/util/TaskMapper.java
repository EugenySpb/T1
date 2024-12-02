package ru.novikov.T1.util;

import org.springframework.stereotype.Component;
import ru.novikov.T1.dto.TaskDTO;
import ru.novikov.T1.models.Task;
import ru.novikov.T1.models.TaskStatus;

@Component
public class TaskMapper {
    public TaskDTO toDto(Task task) {
        return TaskDTO.builder()
                .id(task.getId())
                .title(task.getTitle())
                .description(task.getDescription())
                .userId(task.getUserId())
                .status(task.getStatus().name())
                .build();
    }

    public Task toEntity(TaskDTO dto) {
        return Task.builder()
                .id(dto.getId())
                .title(dto.getTitle())
                .description(dto.getDescription())
                .userId(dto.getUserId())
                .status(TaskStatus.valueOf(dto.getStatus()))
                .build();
    }
}
