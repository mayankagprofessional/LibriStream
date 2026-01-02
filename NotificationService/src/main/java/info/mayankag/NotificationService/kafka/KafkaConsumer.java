package info.mayankag.NotificationService.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import user.events.UserEvent;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaConsumer {

    @KafkaListener(
            topics = "${spring.kafka.topic.name}",
            groupId = "{spring.kafka.consumer.group-id}"
    )
    public void receiveNotificationForUserCreated(UserEvent userEvent) {
        log.info("Received User Event for User ID :{}, Email: {}",
                    userEvent.getUserId(),
                    userEvent.getEmail());

        //TODO: Send Email to User to Notify their account creation
    }
}