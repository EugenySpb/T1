package ru.novikov.T1.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import ru.novikov.T1.dto.TaskDTO;
import ru.novikov.T1.config.EmailProperties;

@Slf4j
@Service
public class NotificationService {

    private final JavaMailSender mailSender;
    private final EmailProperties emailProperties;

    public NotificationService(JavaMailSender mailSender, EmailProperties emailProperties) {
        this.mailSender = mailSender;
        this.emailProperties = emailProperties;
    }

    public void sendNotification(TaskDTO taskDTO) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(emailProperties.getFromEmail());
            message.setTo("sendforapi@yandex.ru");
            message.setSubject("Task status update");
            message.setText(String.format("Task ID: %d\n Your task status has changed. New status: %s", taskDTO.getId(), taskDTO.getStatus()));

            mailSender.send(message);
            log.info("Email sent to task {}", taskDTO.getId());
        } catch (Exception e) {
            log.error("Error to send email to task {}", taskDTO.getId(), e);
        }
    }
}
