package ru.novikov.T1.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import ru.novikov.T1.dto.TaskDTO;
import ru.novikov.T1.services.NotificationService;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class KafkaTaskConsumer {

    private final NotificationService notificationService;

    @KafkaListener(id = "${kafka.consumer.group-id}",
            topics = "${kafka.topic.client_id_registered}",
            containerFactory = "kafkaListenerContainerFactory")
    public void processTaskUpdates(
            @Payload List<TaskDTO> taskDTOList,
            Acknowledgment ack,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
            @Header(KafkaHeaders.RECEIVED_KEY) String key) {

        log.info("Updating tasks from topic '{}', key '{}'", topic, key);

        try {
            for (TaskDTO taskDTO : taskDTOList) {
                try {
                    notificationService.sendNotification(taskDTO);
                    log.info("Successfully notification sent for task: {}", taskDTO);
                } catch (Exception e) {
                    log.error("Failed to send notification for task: {}", taskDTO, e);
                }
            }
            ack.acknowledge();
        } catch (Exception e) {
            log.error("Error updating tasks from topic '{}', key '{}'", topic, key, e);
        }
    }
}
