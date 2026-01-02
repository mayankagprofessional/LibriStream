package info.mayankag.UserProfileService.kafka;

import info.mayankag.UserProfileService.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import user.events.UserEvent;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaProducer {

    @Value("${spring.kafka.topic-name}")
    private String kafkaTopic;

    private final KafkaTemplate<String, UserEvent> kafkaTemplate;

    public void notifyUserCreated(User user) {
        UserEvent event = UserEvent
                .newBuilder()
                .setUserId(user.getId().toString())
                .setFirstName(user.getFirstname())
                .setLastName(user.getLastname())
                .setEmail(user.getEmail())
                .setEventType("USER_CREATED")
                .build();

        Message<UserEvent> userEventMessage = MessageBuilder
                .withPayload(event)
                .setHeader(KafkaHeaders.TOPIC, kafkaTopic)
                .build();

        kafkaTemplate.send(userEventMessage);
    }
}
