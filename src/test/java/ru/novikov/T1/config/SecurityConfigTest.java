package ru.novikov.T1.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import ru.novikov.T1.AbstractContainerBaseTest;
import ru.novikov.T1.dto.TaskDTO;
import ru.novikov.T1.models.Task;
import ru.novikov.T1.repositories.TaskRepository;
import ru.novikov.T1.util.TaskMapper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class SecurityConfigTest extends AbstractContainerBaseTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TaskMapper taskMapper;

    @Test
    @DisplayName("Тест доступа с ролью user")
    @WithMockUser
    public void testPublicEndpoints() throws Exception {
        mockMvc.perform(get("/tasks"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Тест доступа с ролью admin")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void testWithAuthenticatedEndpoints() throws Exception {
        TaskDTO taskDTO = TaskDTO.builder()
                .title("Spring Title")
                .description("Spring Description")
                .userId(1L)
                .status("CREATED")
                .build();

        Task task = taskMapper.toEntity(taskDTO);
        taskRepository.save(task);
        Long taskId = task.getId();

        mockMvc.perform(delete("/tasks/" + taskId))
                .andExpect(status().isOk());
    }

    @Test
    public void testWithoutAuthorization() throws Exception {
        mockMvc.perform(post("/tasks/"))
                .andExpect(status().isUnauthorized());
    }
}