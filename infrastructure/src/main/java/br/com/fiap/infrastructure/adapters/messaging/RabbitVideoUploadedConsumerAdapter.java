package br.com.fiap.infrastructure.adapters.messaging;

import br.com.fiap.domain.ports.in.RegisterVideoUploadedInputPort;
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
public class RabbitVideoUploadedConsumerAdapter {

    private static final Logger log = LoggerFactory.getLogger(RabbitVideoUploadedConsumerAdapter.class);

    private final ObjectMapper objectMapper;
    private final RegisterVideoUploadedInputPort registerVideoUploaded;

    public RabbitVideoUploadedConsumerAdapter(ObjectMapper objectMapper,
                                              RegisterVideoUploadedInputPort registerVideoUploaded) {
        this.objectMapper = objectMapper;
        this.registerVideoUploaded = registerVideoUploaded;
    }

    @RabbitListener(queues = RabbitMqConfiguration.VIDEO_UPLOADED_QUEUE)
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
            String userId = (String) body.get("userId");
            String originalFilename = (String) body.get("originalFilename");
            Long fileSizeBytes = toLong(body.get("fileSizeBytes"));
            String mimeType = (String) body.get("mimeType");
            String storageKey = (String) body.get("storageKey");

            registerVideoUploaded.execute(videoId, userId, originalFilename, fileSizeBytes, mimeType, storageKey);
            log.info("[RabbitMQ] Registered video-uploaded event for videoId={}", videoId);
        } catch (Exception e) {
            log.error("[RabbitMQ] Failed to process video-uploaded message", e);
            throw new IllegalArgumentException("Failed to process video-uploaded message", e);
        }
    }

    private Long toLong(Object value) {
        return value == null ? null : ((Number) value).longValue();
    }
}
