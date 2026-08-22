package br.com.fiap.infrastructure.adapters.messaging;

import br.com.fiap.domain.model.Video;
import br.com.fiap.domain.ports.in.RegisterVideoUploadedInputPort;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("RabbitVideoUploadedConsumerAdapter - Unit Tests")
class RabbitVideoUploadedConsumerAdapterTest {

    @Mock
    private RegisterVideoUploadedInputPort registerVideoUploaded;

    private RabbitVideoUploadedConsumerAdapter consumer;

    @BeforeEach
    void setUp() {
        consumer = new RabbitVideoUploadedConsumerAdapter(new ObjectMapper(), registerVideoUploaded);
    }

    @Test
    @DisplayName("consume parses payload and registers video")
    void consume_validPayload_registersVideo() {
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
        when(registerVideoUploaded.execute(eq(videoId), eq("user-1"), eq("video.mp4"), eq(1024L), eq("video/mp4"), eq("videos/user-1/video.mp4")))
                .thenReturn(Video.createWithId(videoId, "user-1", "video.mp4", 1024L, "video/mp4", "videos/user-1/video.mp4"));

        consumer.consume(body, "video.events", "video.uploaded");

        verify(registerVideoUploaded).execute(videoId, "user-1", "video.mp4", 1024L, "video/mp4", "videos/user-1/video.mp4");
    }

    @Test
    @DisplayName("consume with invalid payload throws and skips use case")
    void consume_invalidPayload_throwsAndSkipsUseCase() {
        assertThatThrownBy(() -> consumer.consume("not-json", null, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("video-uploaded");

        verify(registerVideoUploaded, never()).execute(any(), any(), any(), any(), any(), any());
    }
}
