package com.kalhan.email_service.kafka.consumer;

import com.kalhan.email_service.entity.Email;
import com.kalhan.email_service.kafka.model.EmailTemplateName;
import com.kalhan.email_service.kafka.model.UserRegisteredEvent;
import com.kalhan.email_service.service.EmailService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
@Slf4j
public class UserRegisteredEventConsumer {

    private final EmailService emailService;
    @Value("${application.mailing.frontend.activation-url}")
    private String activationUrl;


    @KafkaListener(topics = "${kafka.topics.user-registered.topic}",
            groupId = "${kafka.topics.user-registered.consumerGroup}",
            containerFactory = "concurrentKafkaListenerContainerFactory"
    )
    public void consumeCreatedUserEvent(@Payload UserRegisteredEvent eventData,
                                        @Headers ConsumerRecord<String, Object> consumerRecord) throws MessagingException {
        log.info("UserCreatedEventConsumer.consumeApprovalRequestResultedEvent consumed EVENT :{} " +
                        "from partition : {} " +
                        "with offset : {} " +
                        "thread : {} " +
                        "for message key: {}",
                eventData, consumerRecord.partition(), consumerRecord.offset(), Thread.currentThread().getName(), consumerRecord.key());

        Email emailEntity = UserRegisteredEvent.getEmailEntityFromEvent(eventData);

        emailService.sendEmail(
                emailEntity.getEmail(),
                emailEntity.getFullName(),
                EmailTemplateName.ACTIVATE_ACCOUNT,
                activationUrl,
                emailEntity.getToken(),
                "Account activation"
        );

    }

}
