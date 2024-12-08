package ru.novikov.T1.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.novikov.T1.AbstractContainerBaseTest;
import ru.novikov.T1.dto.TaskDTO;
import ru.novikov.T1.models.Task;
import ru.novikov.T1.repositories.TaskRepository;
import ru.novikov.T1.util.TaskMapper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class TaskControllerTest extends AbstractContainerBaseTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TaskMapper taskMapper;

    @Autowired
    private ObjectMapper objectMapper;

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
    @WithMockUser
    public void getAllTasks() throws Exception {
        mockMvc.perform(get("/tasks"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].title").value("Spring Title"));
    }

    @Test
    @DisplayName("Тест получения задачи по id")
    @WithMockUser
    public void getTaskById() throws Exception {
        mockMvc.perform(get("/tasks/" + taskId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Spring Title"));
    }

    @Test
    @DisplayName("Ошибка при получении задачи по ID")
    @WithMockUser
    public void exceptions() throws Exception {
        mockMvc.perform(get("/tasks/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Task not found"));
    }

    @Test
    @DisplayName("Тест создания задачи")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void createTask() throws Exception {
        TaskDTO taskDTO = TaskDTO.builder()
                .title("New Task")
                .description("New Task Description")
                .userId(2L)
                .status("CREATED")
                .build();

        mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(taskDTO)))
                .andExpect(status().isCreated())
                .andExpect(content().string("Task created"));
    }

    @Test
    @DisplayName("Тест обновления задачи")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void updateTask() throws Exception {
        TaskDTO taskDTO = TaskDTO.builder()
                .title("Updated Task")
                .description("Updated Task Description")
                .userId(2L)
                .status("CREATED")
                .build();

        mockMvc.perform(put("/tasks/" + taskId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(taskDTO)))
                .andExpect(status().isOk())
                .andExpect(content().string("Task updated"));
    }

    @Test
    @DisplayName("Ошибка при обновлении задачи")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void updateError() throws Exception {
        mockMvc.perform(put("/tasks/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new TaskDTO())))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Task not found"));
    }

    @Test
    @DisplayName("Тест удаления задачи")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void deleteTask() throws Exception {
        mockMvc.perform(delete("/tasks/" + taskId))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Ошибка при удалении задачи по ID")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void deleteError() throws Exception {
        mockMvc.perform(delete("/tasks/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Task not found"));
    }

}