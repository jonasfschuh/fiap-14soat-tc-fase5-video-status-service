package br.com.fiap.infrastructure.adapters.messaging;

import br.com.fiap.domain.ports.in.RegisterVideoUploadedInputPort;
import br.com.fiap.infrastructure.logging.SqsMessageLogger;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.DeleteMessageRequest;
import software.amazon.awssdk.services.sqs.model.Message;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Polls the {@code video-uploaded} SQS queue and registers the initial
 * PENDING status projection for each video that was just uploaded.
 */
public class VideoUploadedSqsConsumer {

    private static final Logger log = LoggerFactory.getLogger(VideoUploadedSqsConsumer.class);
    private static final int MAX_MESSAGES = 10;
    private static final int WAIT_TIME_SECONDS = 5;

    private final SqsClient sqsClient;
    private final ObjectMapper objectMapper;
    private final SqsMessageLogger sqsMessageLogger;
    private final RegisterVideoUploadedInputPort registerVideoUploaded;
    private final String queueUrl;

    public VideoUploadedSqsConsumer(SqsClient sqsClient,
                                     ObjectMapper objectMapper,
                                     SqsMessageLogger sqsMessageLogger,
                                     RegisterVideoUploadedInputPort registerVideoUploaded,
                                     String queueUrl) {
        this.sqsClient = sqsClient;
        this.objectMapper = objectMapper;
        this.sqsMessageLogger = sqsMessageLogger;
        this.registerVideoUploaded = registerVideoUploaded;
        this.queueUrl = queueUrl;
    }

    @Scheduled(fixedDelayString = "${aws.sqs.poll.interval-ms:5000}")
    public void poll() {
        ReceiveMessageRequest request = ReceiveMessageRequest.builder()
                .queueUrl(queueUrl)
                .maxNumberOfMessages(MAX_MESSAGES)
                .waitTimeSeconds(WAIT_TIME_SECONDS)
                .build();

        List<Message> messages = sqsClient.receiveMessage(request).messages();
        for (Message message : messages) {
            processMessage(message);
        }
    }

    void processMessage(Message message) {
        try {
            sqsMessageLogger.logMessageReceived(queueUrl, message.body(), message.messageId(), message.receiptHandle(), null);

            Map<String, Object> payload = objectMapper.readValue(message.body(), new TypeReference<Map<String, Object>>() {});

            UUID videoId = UUID.fromString((String) payload.get("videoId"));
            String userId = (String) payload.get("userId");
            String originalFilename = (String) payload.get("originalFilename");
            Long fileSizeBytes = toLong(payload.get("fileSizeBytes"));
            String mimeType = (String) payload.get("mimeType");
            String storageKey = (String) payload.get("storageKey");

            registerVideoUploaded.execute(videoId, userId, originalFilename, fileSizeBytes, mimeType, storageKey);

            deleteMessage(message);
            log.info("[SQS] Registered video-uploaded event for videoId={}", videoId);
        } catch (Exception e) {
            log.error("[SQS] Failed to process video-uploaded message: {}", message.messageId(), e);
        }
    }

    private void deleteMessage(Message message) {
        sqsClient.deleteMessage(DeleteMessageRequest.builder()
                .queueUrl(queueUrl)
                .receiptHandle(message.receiptHandle())
                .build());
    }

    private Long toLong(Object value) {
        return value == null ? null : ((Number) value).longValue();
    }
}
