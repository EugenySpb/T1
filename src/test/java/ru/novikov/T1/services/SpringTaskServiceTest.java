package ru.novikov.T1.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.novikov.T1.AbstractContainerBaseTest;
import ru.novikov.T1.dto.TaskDTO;
import ru.novikov.T1.kafka.KafkaTaskProducer;
import ru.novikov.T1.models.Task;
import ru.novikov.T1.models.TaskStatus;
import ru.novikov.T1.repositories.TaskRepository;
import ru.novikov.T1.util.TaskMapper;
import ru.novikov.T1.util.TaskNotFoundException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

@SpringBootTest
class SpringTaskServiceTest extends AbstractContainerBaseTest {

    @Autowired
    private TaskService taskService;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TaskMapper taskMapper;

    private Long taskId;

    @BeforeEach
    void setUp() {
        taskRepository.deleteAll();

        TaskDTO taskDTO = TaskDTO.builder()
                .title("Spring Title")
                .description("Spring Description")
                .userId(1L)
                .status("CREATED")
                .build();

        Task task = taskMapper.toEntity(taskDTO);
        taskRepository.save(task);
        taskId = task.getId();
    }

    @Test
    @DisplayName("Тест получения всех задач")
    public void getAllTasks() {
        Task task = new Task(2L, "Another Spring Title", "Another Spring Description", 2L, TaskStatus.CREATED);
        taskRepository.save(task);

        List<TaskDTO> tasks = taskService.getAllTasks();

        assertNotNull(tasks);
        assertEquals(2, tasks.size());
        assertEquals("Spring Title", tasks.get(0).getTitle());
        assertEquals("Another Spring Title", tasks.get(1).getTitle());
        assertEquals(1L, tasks.get(0).getUserId());
        assertEquals(2L, tasks.get(1).getUserId());
    }

    @Test
    @DisplayName("Тест получения задачи по id")
    public void getTaskById() {
        TaskDTO actualResult = taskService.getTaskById(taskId);

        assertNotNull(actualResult);

        assertEquals(taskId, actualResult.getId());
        assertEquals("Spring Title", actualResult.getTitle());
        assertEquals("Spring Description", actualResult.getDescription());
        assertEquals(1L, actualResult.getUserId());
        assertEquals("CREATED", actualResult.getStatus());
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
                .title("Spring Task Create")
                .description("Spring Task Description")
                .userId(1L)
                .status("CREATED")
                .build();

        String result = taskService.createTask(taskDTO);

        assertNotNull(result);
        assertEquals("Task created", result);

        Task createdTask = taskRepository.findAll().stream()
                .filter(task -> "Spring Task Create".equals(task.getTitle()))
                .findFirst()
                .orElseThrow();

        assertEquals("Spring Task Create", createdTask.getTitle());
        assertEquals("Spring Task Description", createdTask.getDescription());
        assertEquals(TaskStatus.CREATED, createdTask.getStatus());
    }

    @Test
    @DisplayName("Тест обновления задачи")
    public void updateTask() {
        KafkaTaskProducer kafkaMock = mock(KafkaTaskProducer.class);
        TaskService taskService = new TaskService(taskRepository, kafkaMock, taskMapper);

        Task existTask = taskRepository.findById(taskId).orElseThrow();

        TaskDTO updatedTaskDTO = TaskDTO.builder()
                .title("Updated Spring Task")
                .description("Updated Spring Description")
                .userId(1L)
                .status("UPDATED")
                .build();

        String result = taskService.updateTask(taskId, updatedTaskDTO);

        assertNotNull(result);
        assertEquals("Task updated", result);

        Task updatedTask = taskRepository.findById(taskId).orElseThrow();
        assertEquals("Updated Spring Task", updatedTask.getTitle());
        assertEquals("Updated Spring Description", updatedTask.getDescription());
        assertEquals(TaskStatus.UPDATED, updatedTask.getStatus());

        assertNotEquals(existTask.getTitle(), updatedTask.getTitle());
        assertNotEquals(existTask.getDescription(), updatedTask.getDescription());
        assertNotEquals(existTask.getStatus(), updatedTask.getStatus());
    }

    @Test
    @DisplayName("Тест удаления задачи")
    public void deleteTask() {
        taskService.deleteTask(taskId);
        assertFalse(taskRepository.existsById(taskId));
    }

    @Test
    @DisplayName("Тест ошибки при удалении задачи по id")
    public void deleteError() {
        assertThrows(TaskNotFoundException.class, () -> taskService.deleteTask(99L));
    }
}