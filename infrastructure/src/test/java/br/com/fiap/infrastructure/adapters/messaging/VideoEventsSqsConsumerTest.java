package br.com.fiap.infrastructure.adapters.messaging;

import br.com.fiap.domain.model.Video;
import br.com.fiap.domain.model.VideoStatus;
import br.com.fiap.domain.ports.in.UpdateVideoStatusInputPort;
import br.com.fiap.infrastructure.logging.SqsMessageLogger;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.DeleteMessageRequest;
import software.amazon.awssdk.services.sqs.model.Message;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageResponse;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("VideoEventsSqsConsumer - Unit Tests")
class VideoEventsSqsConsumerTest {

    private static final String QUEUE_URL = "http://localhost:4566/000000000000/video-events";

    @Mock private SqsClient sqsClient;
    @Mock private SqsMessageLogger sqsMessageLogger;
    @Mock private UpdateVideoStatusInputPort updateVideoStatus;

    private VideoEventsSqsConsumer consumer;

    @BeforeEach
    void setUp() {
        consumer = new VideoEventsSqsConsumer(sqsClient, new ObjectMapper(), sqsMessageLogger, updateVideoStatus, QUEUE_URL);
    }

    @Test
    @DisplayName("processMessage parses payload, updates status and deletes message")
    void processMessage_validPayload_updatesAndDeletes() {
        UUID videoId = UUID.randomUUID();
        String body = """
                {"videoId": "%s", "status": "PROCESSING", "timestamp": "2025-01-01T10:00:00Z"}
                """.formatted(videoId);
        Message message = Message.builder().messageId("msg-1").receiptHandle("receipt-1").body(body).build();
        when(updateVideoStatus.execute(eq(videoId), eq(VideoStatus.PROCESSING)))
                .thenReturn(Video.createWithId(videoId, "user-1", "f.mp4", 10L, "video/mp4", "k"));

        consumer.processMessage(message);

        verify(updateVideoStatus).execute(videoId, VideoStatus.PROCESSING);
        verify(sqsClient).deleteMessage(any(DeleteMessageRequest.class));
        verify(sqsMessageLogger).logMessageReceived(eq(QUEUE_URL), eq(body), eq("msg-1"), eq("receipt-1"), isNull());
    }

    @Test
    @DisplayName("processMessage with lowercase status normalizes to enum")
    void processMessage_lowercaseStatus_normalizesToUpperCase() {
        UUID videoId = UUID.randomUUID();
        String body = """
                {"videoId": "%s", "status": "done"}
                """.formatted(videoId);
        Message message = Message.builder().messageId("msg-2").receiptHandle("receipt-2").body(body).build();
        when(updateVideoStatus.execute(eq(videoId), eq(VideoStatus.DONE)))
                .thenReturn(Video.createWithId(videoId, "user-1", "f.mp4", 10L, "video/mp4", "k"));

        consumer.processMessage(message);

        verify(updateVideoStatus).execute(videoId, VideoStatus.DONE);
    }

    @Test
    @DisplayName("processMessage with invalid payload logs error and does not delete message")
    void processMessage_invalidPayload_doesNotThrowAndSkipsDeletion() {
        Message message = Message.builder().messageId("msg-3").receiptHandle("receipt-3").body("not-json").build();

        assertThatCode(() -> consumer.processMessage(message)).doesNotThrowAnyException();

        verify(sqsClient, never()).deleteMessage(any(DeleteMessageRequest.class));
        verify(updateVideoStatus, never()).execute(any(), any());
    }

    @Test
    @DisplayName("processMessage with unknown status value logs error and skips deletion")
    void processMessage_unknownStatus_doesNotThrowAndSkipsDeletion() {
        Message message = Message.builder().messageId("msg-4").receiptHandle("receipt-4")
                .body("{\"videoId\": \"" + UUID.randomUUID() + "\", \"status\": \"UNKNOWN_STATUS\"}").build();

        assertThatCode(() -> consumer.processMessage(message)).doesNotThrowAnyException();

        verify(sqsClient, never()).deleteMessage(any(DeleteMessageRequest.class));
    }

    @Test
    @DisplayName("poll receives messages and processes each one")
    void poll_processesReceivedMessages() {
        UUID videoId = UUID.randomUUID();
        String body = "{\"videoId\": \"" + videoId + "\", \"status\": \"FAILED\"}";
        Message message = Message.builder().messageId("msg-1").receiptHandle("receipt-1").body(body).build();
        when(sqsClient.receiveMessage(any(ReceiveMessageRequest.class)))
                .thenReturn(ReceiveMessageResponse.builder().messages(message).build());
        when(updateVideoStatus.execute(any(), any()))
                .thenReturn(Video.createWithId(videoId, "user-1", "f.mp4", 10L, "video/mp4", "k"));

        consumer.poll();

        verify(sqsClient).receiveMessage(any(ReceiveMessageRequest.class));
        verify(updateVideoStatus).execute(videoId, VideoStatus.FAILED);
    }
}
