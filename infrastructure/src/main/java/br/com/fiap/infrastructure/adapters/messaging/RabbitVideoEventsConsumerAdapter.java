package br.com.fiap.infrastructure.adapters.messaging;

import br.com.fiap.domain.exceptions.VideoNotFoundException;
import br.com.fiap.domain.model.VideoStatus;
import br.com.fiap.domain.ports.in.RegisterVideoUploadedInputPort;
import br.com.fiap.domain.ports.in.UpdateVideoStatusInputPort;
import br.com.fiap.infrastructure.configuration.RabbitMqConfiguration;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;

@Component
public class RabbitVideoEventsConsumerAdapter {

    private static final Logger log = LoggerFactory.getLogger(RabbitVideoEventsConsumerAdapter.class);

    private final ObjectMapper objectMapper;
    private final UpdateVideoStatusInputPort updateVideoStatus;
    private final RegisterVideoUploadedInputPort registerVideoUploaded;

    public RabbitVideoEventsConsumerAdapter(ObjectMapper objectMapper,
                                            UpdateVideoStatusInputPort updateVideoStatus,
                                            RegisterVideoUploadedInputPort registerVideoUploaded) {
        this.objectMapper = objectMapper;
        this.updateVideoStatus = updateVideoStatus;
        this.registerVideoUploaded = registerVideoUploaded;
    }

    @RabbitListener(queues = RabbitMqConfiguration.VIDEO_EVENTS_QUEUE)
    public void consume(String payload,
                        @Header(name = AmqpHeaders.RECEIVED_EXCHANGE, required = false) String exchange,
                        @Header(name = AmqpHeaders.RECEIVED_ROUTING_KEY, required = false) String routingKey) {
        try {
            String ex = exchange != null ? exchange : "N/A";
            String rk = routingKey != null ? routingKey : "N/A";
            log.info("<<< Payload recebido do RabbitMQ — exchange [{}] routing-key [{}]:\n{}",
                    ex, rk, RabbitPayloadLogFormatter.prettyJson(objectMapper, payload));

            String json = payload.trim();
            if (json.startsWith("\"")) {
                json = objectMapper.readValue(json, String.class);
            }
            Map<String, Object> body = objectMapper.readValue(json, new TypeReference<Map<String, Object>>() {});

            UUID videoId = UUID.fromString((String) body.get("videoId"));
            VideoStatus status = VideoStatus.valueOf(((String) body.get("status")).toUpperCase());
            String outputKey = resolveOutputKey(body);

            try {
                updateVideoStatus.execute(videoId, status, outputKey);
                log.info("[RabbitMQ] Applied video-events status={} outputKey={} for videoId={}", status, outputKey, videoId);
            } catch (VideoNotFoundException e) {
                handleOrphanEvent(body, videoId, status, outputKey);
            }

        } catch (VideoNotFoundException e) {
            throw e;
        } catch (Exception e) {
            log.error("[RabbitMQ] Failed to process video-events message", e);
        }
    }

    /**
     * Handles a video-events message whose videoId has no matching record in the DB.
     * This can happen when the video-status-uploaded event was lost or processed before
     * this service started. A minimal record is created from the available payload data
     * and the status is applied immediately.
     */
    private void handleOrphanEvent(Map<String, Object> body, UUID videoId, VideoStatus status, String outputKey) {
        String userId = (String) body.getOrDefault("userId", "unknown");
        String originalFilename = (String) body.getOrDefault("originalFilename", "unknown");
        Long fileSizeBytes = toLong(body.get("fileSizeBytes"), 0L);
        String mimeType = (String) body.getOrDefault("mimeType", "unknown");
        String storageKey = resolveStorageKey(body, outputKey);

        log.warn("[RabbitMQ] Video not found in DB for videoId={}. Creating minimal record and applying status={}.",
                videoId, status);

        registerVideoUploaded.execute(videoId, userId, originalFilename, fileSizeBytes, mimeType, storageKey);
        updateVideoStatus.execute(videoId, status, outputKey);

        log.info("[RabbitMQ] Orphan video-events reconciled: videoId={} status={} outputKey={}", videoId, status, outputKey);
    }

    /**
     * Resolves the original video storage key from the payload.
     * Prefers {@code storageAbsolutePath} (original uploaded file path),
     * falls back to {@code outputKey} (processed output key), then "unknown".
     */
    private String resolveStorageKey(Map<String, Object> body, String fallback) {
        String storageAbsolutePath = (String) body.get("storageAbsolutePath");
        if (storageAbsolutePath != null && !storageAbsolutePath.isBlank()) {
            return storageAbsolutePath;
        }
        return fallback != null ? fallback : "unknown";
    }

    private Long toLong(Object value, Long defaultValue) {
        return value == null ? defaultValue : ((Number) value).longValue();
    }

    /**
     * Resolves the output key from the payload, preferring {@code outputAbsolutePath}
     * (local/shared filesystem path) over {@code outputKey} (relative storage key).
     * Returns {@code null} if neither is present.
     */
    private String resolveOutputKey(Map<String, Object> body) {
        String absolutePath = (String) body.get("outputAbsolutePath");
        if (absolutePath != null && !absolutePath.isBlank()) {
            return absolutePath;
        }
        return (String) body.get("outputKey");
    }
}
