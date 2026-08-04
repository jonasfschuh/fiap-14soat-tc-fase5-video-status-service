package br.com.fiap.infrastructure.adapters.messaging;

import br.com.fiap.domain.model.Video;
import br.com.fiap.domain.ports.in.RegisterVideoUploadedInputPort;
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
@DisplayName("VideoUploadedSqsConsumer - Unit Tests")
class VideoUploadedSqsConsumerTest {

    private static final String QUEUE_URL = "http://localhost:4566/000000000000/video-uploaded";

    @Mock private SqsClient sqsClient;
    @Mock private SqsMessageLogger sqsMessageLogger;
    @Mock private RegisterVideoUploadedInputPort registerVideoUploaded;

    private VideoUploadedSqsConsumer consumer;

    @BeforeEach
    void setUp() {
        consumer = new VideoUploadedSqsConsumer(sqsClient, new ObjectMapper(), sqsMessageLogger, registerVideoUploaded, QUEUE_URL);
    }

    @Test
    @DisplayName("processMessage parses payload, registers video and deletes message")
    void processMessage_validPayload_registersAndDeletes() {
        UUID videoId = UUID.randomUUID();
        String body = """
                {
                  "videoId": "%s",
                  "userId": "user-1",
                  "storageKey": "videos/user-1/video.mp4",
                  "originalFilename": "video.mp4",
                  "fileSizeBytes": 1024,
                  "mimeType": "video/mp4",
                  "timestamp": "2025-01-01T10:00:00Z"
                }
                """.formatted(videoId);
        Message message = Message.builder().messageId("msg-1").receiptHandle("receipt-1").body(body).build();
        when(registerVideoUploaded.execute(eq(videoId), eq("user-1"), eq("video.mp4"), eq(1024L), eq("video/mp4"), eq("videos/user-1/video.mp4")))
                .thenReturn(Video.createWithId(videoId, "user-1", "video.mp4", 1024L, "video/mp4", "videos/user-1/video.mp4"));

        consumer.processMessage(message);

        verify(registerVideoUploaded).execute(videoId, "user-1", "video.mp4", 1024L, "video/mp4", "videos/user-1/video.mp4");
        verify(sqsClient).deleteMessage(any(DeleteMessageRequest.class));
        verify(sqsMessageLogger).logMessageReceived(eq(QUEUE_URL), eq(body), eq("msg-1"), eq("receipt-1"), isNull());
    }

    @Test
    @DisplayName("processMessage with invalid payload logs error and does not delete message")
    void processMessage_invalidPayload_doesNotThrowAndSkipsDeletion() {
        Message message = Message.builder().messageId("msg-2").receiptHandle("receipt-2").body("not-json").build();

        assertThatCode(() -> consumer.processMessage(message)).doesNotThrowAnyException();

        verify(sqsClient, never()).deleteMessage(any(DeleteMessageRequest.class));
        verify(registerVideoUploaded, never()).execute(any(), any(), any(), any(), any(), any());
    }

    @Test
    @DisplayName("poll receives messages and processes each one")
    void poll_processesReceivedMessages() {
        UUID videoId = UUID.randomUUID();
        String body = """
                {"videoId": "%s", "userId": "user-1", "storageKey": "k", "originalFilename": "f.mp4", "fileSizeBytes": 10, "mimeType": "video/mp4"}
                """.formatted(videoId);
        Message message = Message.builder().messageId("msg-1").receiptHandle("receipt-1").body(body).build();
        when(sqsClient.receiveMessage(any(ReceiveMessageRequest.class)))
                .thenReturn(ReceiveMessageResponse.builder().messages(message).build());
        when(registerVideoUploaded.execute(any(), any(), any(), any(), any(), any()))
                .thenReturn(Video.createWithId(videoId, "user-1", "f.mp4", 10L, "video/mp4", "k"));

        consumer.poll();

        verify(sqsClient).receiveMessage(any(ReceiveMessageRequest.class));
        verify(registerVideoUploaded).execute(eq(videoId), eq("user-1"), eq("f.mp4"), eq(10L), eq("video/mp4"), eq("k"));
    }
}
