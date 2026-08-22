package br.com.fiap.infrastructure.configuration;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("RabbitMqConfiguration - Unit Tests")
class RabbitMqConfigurationTest {

    private final RabbitMqConfiguration config = new RabbitMqConfiguration();

    @Test
    @DisplayName("videoEventsExchange returns durable topic exchange")
    void videoEventsExchange_returnsDurableTopicExchange() {
        TopicExchange exchange = config.videoEventsExchange();

        assertThat(exchange.getName()).isEqualTo(RabbitMqConfiguration.VIDEO_EVENTS_EXCHANGE);
        assertThat(exchange.isDurable()).isTrue();
    }

    @Test
    @DisplayName("videoUploadedQueue declares dedicated queue with correct DLX settings")
    void videoUploadedQueue_declaresDedicatedQueueWithDlxSettings() {
        Queue queue = config.videoUploadedQueue();

        assertThat(queue.getName()).isEqualTo(RabbitMqConfiguration.VIDEO_UPLOADED_QUEUE);
        assertThat(queue.isDurable()).isTrue();
        assertThat(queue.getArguments())
                .containsEntry("x-dead-letter-exchange", RabbitMqConfiguration.VIDEO_EVENTS_EXCHANGE)
                .containsEntry("x-dead-letter-routing-key", RabbitMqConfiguration.VIDEO_UPLOADED_DLQ_RK);
    }

    @Test
    @DisplayName("videoEventsQueue declares DLX settings")
    void videoEventsQueue_declaresDlqSettings() {
        Queue queue = config.videoEventsQueue();

        assertThat(queue.getName()).isEqualTo(RabbitMqConfiguration.VIDEO_EVENTS_QUEUE);
        assertThat(queue.isDurable()).isTrue();
        assertThat(queue.getArguments())
                .containsEntry("x-dead-letter-exchange", RabbitMqConfiguration.VIDEO_EVENTS_EXCHANGE)
                .containsEntry("x-dead-letter-routing-key", RabbitMqConfiguration.VIDEO_EVENTS_DLQ_RK);
    }

    @Test
    @DisplayName("bindings route upload and event keys to expected destinations")
    void bindings_routeExpectedKeys() {
        Queue uploadQueue = config.videoUploadedQueue();
        Queue eventQueue  = config.videoEventsQueue();
        TopicExchange exchange = config.videoEventsExchange();

        Binding uploadBinding    = config.videoUploadedBinding(uploadQueue, exchange);
        Binding processedBinding = config.videoProcessedBinding(eventQueue, exchange);
        Binding failedBinding    = config.videoFailedBinding(eventQueue, exchange);

        assertThat(uploadBinding.getExchange()).isEqualTo(RabbitMqConfiguration.VIDEO_EVENTS_EXCHANGE);
        assertThat(uploadBinding.getRoutingKey()).isEqualTo("video.uploaded");
        assertThat(uploadBinding.getDestination()).isEqualTo(RabbitMqConfiguration.VIDEO_UPLOADED_QUEUE);
        assertThat(processedBinding.getDestination()).isEqualTo(RabbitMqConfiguration.VIDEO_EVENTS_QUEUE);
        assertThat(processedBinding.getRoutingKey()).isEqualTo("video.processed");
        assertThat(failedBinding.getDestination()).isEqualTo(RabbitMqConfiguration.VIDEO_EVENTS_QUEUE);
        assertThat(failedBinding.getRoutingKey()).isEqualTo("video.failed");
    }
}
