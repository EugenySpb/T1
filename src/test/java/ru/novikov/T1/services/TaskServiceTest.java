package ru.novikov.T1.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.novikov.T1.dto.TaskDTO;
import ru.novikov.T1.kafka.KafkaTaskProducer;
import ru.novikov.T1.models.Task;
import ru.novikov.T1.models.TaskStatus;
import ru.novikov.T1.repositories.TaskRepository;
import ru.novikov.T1.util.TaskMapper;
import ru.novikov.T1.util.TaskNotFoundException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private KafkaTaskProducer kafkaTaskProducer;

    @Mock
    private TaskMapper taskMapper;

    private TaskService taskService;

    private Task task;

    private TaskDTO taskDTO;

    private Task task2;

    private TaskDTO taskDTO2;

    @BeforeEach
    void setUp() {
        taskService = new TaskService(taskRepository, kafkaTaskProducer, taskMapper);

        task = new Task(1L, "Mock Title", "Mock Description", 1L, TaskStatus.CREATED);
        taskDTO = TaskDTO.builder()
                .id(1L)
                .title("Mock Title")
                .description("Mock Description")
                .userId(1L)
                .status("CREATED")
                .build();

        task2 = new Task(2L, "Another Mock Title", "Another Mock Description", 2L, TaskStatus.CREATED);
        taskDTO2 = TaskDTO.builder()
                .id(2L)
                .title("Another Mock Title")
                .description("Another Mock Description")
                .userId(2L)
                .status("CREATED")
                .build();

        lenient().when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        lenient().when(taskMapper.toDto(task)).thenReturn(taskDTO);
    }

    @Test
    @DisplayName("Тест получения всех задач")
    public void getAllTasks() {
        when(taskRepository.findAll()).thenReturn(List.of(task, task2));
        when(taskMapper.toDto(task)).thenReturn(taskDTO);
        when(taskMapper.toDto(task2)).thenReturn(taskDTO2);

        List<TaskDTO> tasks = taskService.getAllTasks();

        assertNotNull(tasks);
        assertEquals(2, tasks.size());
        assertEquals("Mock Title", tasks.get(0).getTitle());
        assertEquals("Another Mock Title", tasks.get(1).getTitle());
    }

    @Test
    @DisplayName("Тест получения задачи по id")
    public void getTaskById() {
        TaskDTO actualResult = taskService.getTaskById(task.getId());

        assertNotNull(actualResult);
        assertEquals(1L, actualResult.getId());
        assertEquals("Mock Title", actualResult.getTitle());
        assertEquals("Mock Description", actualResult.getDescription());
        assertEquals(1L, actualResult.getUserId());
    }

    @Test
    @DisplayName("Тест ошибки при получении задачи по id")
    public void getTaskByIdError() {
        assertThrows(TaskNotFoundException.class, () -> taskService.getTaskById(99L));
    }

    @Test
    @DisplayName("Тест создания задачи")
    public void createTask() {
        TaskDTO taskDTO = TaskDTO.builder()
                .title("Mock Task")
                .description("Mock Task Description")
                .userId(1L)
                .status("CREATED")
                .build();

        Task task = new Task(null, "New Task", "New Description", 1L, TaskStatus.CREATED);

        when(taskMapper.toEntity(taskDTO)).thenReturn(task);
        when(taskRepository.save(task)).thenReturn(task);

        String result = taskService.createTask(taskDTO);

        assertNotNull(result);
        assertEquals("Task created", result);
    }

    @Test
    @DisplayName("Тест обновления задачи")
    public void updateTask() {
        Task existTask = new Task(1L, "Old Mock Task", "Old Mock Description", 1L, TaskStatus.CREATED);
        TaskDTO updatedTaskDTO = TaskDTO.builder()
                .title("Updated Mock Task")
                .description("Updated Mock Description")
                .userId(1L)
                .status("UPDATED")
                .build();

        when(taskRepository.findById(1L)).thenReturn(Optional.of(existTask));

        String result = taskService.updateTask(1L, updatedTaskDTO);

        assertNotNull(result);
        assertEquals("Task updated", result);
        assertEquals("Updated Mock Task", existTask.getTitle());
        assertEquals("Updated Mock Description", existTask.getDescription());
        assertEquals(TaskStatus.UPDATED, existTask.getStatus());
    }

    @Test
    @DisplayName("Тест удаления задачи")
    public void deleteTask() {
        when(taskRepository.findById(task2.getId())).thenReturn(Optional.of(task2));
        taskService.deleteTask(task2.getId());
        assertFalse(taskRepository.existsById(task2.getId()));
    }

    @Test
    @DisplayName("Тест ошибки при удалении задачи по id")
    public void deleteError() {
        assertThrows(TaskNotFoundException.class, () -> taskService.deleteTask(99L));
    }
}