package ru.novikov.T1.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.novikov.T1.aspect.LogBeforeAspect;
import ru.novikov.T1.models.Task;
import ru.novikov.T1.services.TaskService;

import java.util.List;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping
    @LogBeforeAspect
    public List<Task> getAllTasks() {
        return taskService.getAllTasks();
    }

    @GetMapping("/{id}")
    @LogBeforeAspect
    public Task getTaskById(@PathVariable Long id) {
        return taskService.getTaskById(id);
    }

    @PostMapping
    @LogBeforeAspect
    public ResponseEntity<String> createTask(@RequestBody Task task) {
        taskService.createTask(task);
        return ResponseEntity.ok("Task created");
    }

    @PutMapping("/{id}")
    @LogBeforeAspect
    public ResponseEntity<String> updateTask(@PathVariable Long id, @RequestBody Task task) {
        taskService.updateTask(id, task);
        return ResponseEntity.ok("Task updated");
    }

    @DeleteMapping("/{id}")
    @LogBeforeAspect
    public ResponseEntity<String> deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
        return ResponseEntity.ok("Task deleted");
    }
}
