package info.mayankag.UserProfileService.kafka;

import info.mayankag.UserProfileService.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import user.events.UserEvent;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaProducer {

    private final KafkaTemplate<String, byte[]> kafkaTemplate;

    public void notifyUserCreated(User user) {
        UserEvent event = UserEvent
                .newBuilder()
                .setUserId(user.getId().toString())
                .setFirstName(user.getFirstname())
                .setLastName(user.getLastname())
                .setEmail(user.getEmail())
                .setEventType("USER_CREATED")
                .build();

        try {
            kafkaTemplate.send("user", event.toByteArray());
        } catch (Exception e) {
            log.error("Error sending UserCreatedEvent to Kafka: {}", event);
        }
    }
}
