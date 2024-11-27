package ru.novikov.T1.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.novikov.T1.aspect.LogBeforeAspect;
import ru.novikov.T1.dto.TaskDTO;
import ru.novikov.T1.models.Task;
import ru.novikov.T1.services.TaskService;
import ru.novikov.T1.util.TaskMapper;

import java.util.List;

@RestController
@RequestMapping("/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;
    private final TaskMapper taskMapper;

    @GetMapping
    @LogBeforeAspect
    public List<TaskDTO> getAllTasks() {
        return taskService.getAllTasks()
                .stream()
                .map(taskMapper::toDto)
                .toList();
    }

    @GetMapping("/{id}")
    @LogBeforeAspect
    public TaskDTO getTaskById(@PathVariable Long id) {
        Task task = taskService.getTaskById(id);
        return taskMapper.toDto(task);
    }

    @PostMapping
    @LogBeforeAspect
    @ResponseStatus(HttpStatus.CREATED)
    public String createTask(@RequestBody TaskDTO taskDTO) {
        Task task = taskMapper.toEntity(taskDTO);
        return taskService.createTask(task);
    }

    @PutMapping("/{id}")
    @LogBeforeAspect
    @ResponseStatus(HttpStatus.OK)
    public String updateTask(@PathVariable Long id, @RequestBody TaskDTO taskDTO) {
        Task task = taskMapper.toEntity(taskDTO);
        return taskService.updateTask(id, task);
    }

    @DeleteMapping("/{id}")
    @LogBeforeAspect
    @ResponseStatus(HttpStatus.OK)
    public void deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
    }
}
