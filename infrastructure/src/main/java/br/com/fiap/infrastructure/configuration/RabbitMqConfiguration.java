package br.com.fiap.infrastructure.configuration;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableRabbit
public class RabbitMqConfiguration {

    public static final String VIDEO_EVENTS_EXCHANGE        = "video.events";

    // Fila exclusiva deste serviço para eventos video.uploaded
    // (fila dedicada evita competing-consumer com o video-processing-service)
    public static final String VIDEO_UPLOADED_QUEUE         = "video-status-uploaded";
    public static final String VIDEO_UPLOADED_DLQ           = "video-status-uploaded-dlq";
    public static final String VIDEO_UPLOADED_DLQ_RK        = "video.status.uploaded.dlq";

    public static final String VIDEO_EVENTS_QUEUE           = "video-events";
    public static final String VIDEO_EVENTS_DLQ             = "video-events-dlq";
    public static final String VIDEO_EVENTS_DLQ_RK          = "video.events.dlq";

    @Bean
    public TopicExchange videoEventsExchange() {
        return new TopicExchange(VIDEO_EVENTS_EXCHANGE, true, false);
    }

    // ── video-status-uploaded ─────────────────────────────────────────────────

    @Bean
    public Queue videoUploadedQueue() {
        return QueueBuilder.durable(VIDEO_UPLOADED_QUEUE)
                .deadLetterExchange(VIDEO_EVENTS_EXCHANGE)
                .deadLetterRoutingKey(VIDEO_UPLOADED_DLQ_RK)
                .build();
    }

    @Bean
    public Queue videoUploadedDlq() {
        return QueueBuilder.durable(VIDEO_UPLOADED_DLQ).build();
    }

    @Bean
    public Binding videoUploadedBinding(@Qualifier("videoUploadedQueue") Queue videoUploadedQueue,
                                        TopicExchange videoEventsExchange) {
        return BindingBuilder.bind(videoUploadedQueue).to(videoEventsExchange).with("video.uploaded");
    }

    @Bean
    public Binding videoUploadedDlqBinding(@Qualifier("videoUploadedDlq") Queue videoUploadedDlq,
                                           TopicExchange videoEventsExchange) {
        return BindingBuilder.bind(videoUploadedDlq).to(videoEventsExchange).with(VIDEO_UPLOADED_DLQ_RK);
    }

    // ── video-events ──────────────────────────────────────────────────────────

    @Bean
    public Queue videoEventsQueue() {
        return QueueBuilder.durable(VIDEO_EVENTS_QUEUE)
                .deadLetterExchange(VIDEO_EVENTS_EXCHANGE)
                .deadLetterRoutingKey(VIDEO_EVENTS_DLQ_RK)
                .build();
    }

    @Bean
    public Queue videoEventsDlq() {
        return QueueBuilder.durable(VIDEO_EVENTS_DLQ).build();
    }

    @Bean
    public Binding videoProcessedBinding(@Qualifier("videoEventsQueue") Queue videoEventsQueue,
                                         TopicExchange videoEventsExchange) {
        return BindingBuilder.bind(videoEventsQueue).to(videoEventsExchange).with("video.processed");
    }

    @Bean
    public Binding videoFailedBinding(@Qualifier("videoEventsQueue") Queue videoEventsQueue,
                                      TopicExchange videoEventsExchange) {
        return BindingBuilder.bind(videoEventsQueue).to(videoEventsExchange).with("video.failed");
    }

    @Bean
    public Binding videoEventsDlqBinding(@Qualifier("videoEventsDlq") Queue videoEventsDlq,
                                         TopicExchange videoEventsExchange) {
        return BindingBuilder.bind(videoEventsDlq).to(videoEventsExchange).with(VIDEO_EVENTS_DLQ_RK);
    }
}
