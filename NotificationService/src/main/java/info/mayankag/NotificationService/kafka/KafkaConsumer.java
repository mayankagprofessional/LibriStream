package info.mayankag.NotificationService.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.DltHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.retrytopic.TopicSuffixingStrategy;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Component;
import user.events.UserEvent;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaConsumer {

    @RetryableTopic(
            attempts = "4", // 1 initial attempt + 3 retries
            backoff = @Backoff(delay = 12000, multiplier = 2.0), // Exponential: 12s, 24s, 48s
            topicSuffixingStrategy = TopicSuffixingStrategy.SUFFIX_WITH_INDEX_VALUE
    )
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

    /**
     * Dead Letter Topic (DLT) Handler.
     * This method is automatically invoked by Spring Kafka when a message has
     * exhausted all configured retry attempts defined in the @RetryableTopic.
     * Key Responsibilities:
     * 1. Final Failure Logging: Captures the payload that could not be processed.
     * 2. Observability: Provides metadata (like the original topic) for troubleshooting.
     * 3. Terminal Action: This is the place to perform manual intervention tasks,
     * such as saving the failed message to a database table or sending an
     * alert to the DevOps team.
     * @param data   The original message payload that failed processing.
     * @param topic  The name of the original topic where the message first arrived.
     */
    @DltHandler
    public void handleDlt(String data, @Header(KafkaHeaders.ORIGINAL_TOPIC) String topic) {
        log.error("Email failed after all retries. Message saved in DLT: {}, topic {}", data, topic);
    }
}