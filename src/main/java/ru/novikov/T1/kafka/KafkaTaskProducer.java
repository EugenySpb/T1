package ru.novikov.T1.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import ru.novikov.T1.dto.TaskDTO;
import ru.novikov.T1.models.TaskStatus;

@Slf4j
@RequiredArgsConstructor
@Component
public class KafkaTaskProducer {

    private final KafkaTemplate<String, TaskDTO> kafkaTemplate;

    public void sendTaskUpdate(Long taskId, TaskStatus status) {
        TaskDTO dto = TaskDTO.builder()
                .id(taskId)
                .status(status.name())
                .build();
        try {
            kafkaTemplate.sendDefault(taskId.toString(), dto);
            log.info("Sent task update: id={}, status={}", taskId, status);
        } catch (Exception ex) {
            log.error("Error to send task update: id={}, status={}", taskId, status, ex);
        }
    }
}
