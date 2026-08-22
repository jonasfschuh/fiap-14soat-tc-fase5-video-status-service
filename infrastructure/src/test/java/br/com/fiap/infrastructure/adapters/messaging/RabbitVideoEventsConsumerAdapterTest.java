package br.com.fiap.infrastructure.adapters.messaging;

import br.com.fiap.domain.exceptions.VideoNotFoundException;
import br.com.fiap.domain.model.Video;
import br.com.fiap.domain.model.VideoStatus;
import br.com.fiap.domain.ports.in.RegisterVideoUploadedInputPort;
import br.com.fiap.domain.ports.in.UpdateVideoStatusInputPort;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("RabbitVideoEventsConsumerAdapter - Unit Tests")
class RabbitVideoEventsConsumerAdapterTest {

    @Mock private UpdateVideoStatusInputPort updateVideoStatus;
    @Mock private RegisterVideoUploadedInputPort registerVideoUploaded;

    private RabbitVideoEventsConsumerAdapter consumer;

    @BeforeEach
    void setUp() {
        consumer = new RabbitVideoEventsConsumerAdapter(new ObjectMapper(), updateVideoStatus, registerVideoUploaded);
    }

    @Test
    @DisplayName("consume parses payload and updates status (no output key in payload)")
    void consume_validPayload_updatesStatus() {
        UUID videoId = UUID.randomUUID();
        String body = String.format("{\"videoId\": \"%s\", \"status\": \"PROCESSING\"}", videoId);

        consumer.consume(body, "video.events", "video.processed");

        verify(updateVideoStatus).execute(videoId, VideoStatus.PROCESSING, null);
    }

    @Test
    @DisplayName("consume prefers outputAbsolutePath over outputKey when both present")
    void consume_prefersOutputAbsolutePathOverOutputKey() {
        UUID videoId = UUID.randomUUID();
        String absolutePath = "processed/jonasfs/frames.zip";
        String body = String.format(
                "{\"videoId\": \"%s\", \"status\": \"DONE\", \"outputKey\": \"outputs/user/frames.zip\", \"outputAbsolutePath\": \"%s\"}",
                videoId, absolutePath);

        consumer.consume(body, "video.events", "video.processed");

        verify(updateVideoStatus).execute(videoId, VideoStatus.DONE, absolutePath);
    }

    @Test
    @DisplayName("consume falls back to outputKey when outputAbsolutePath is absent")
    void consume_fallsBackToOutputKeyWhenAbsolutePathAbsent() {
        UUID videoId = UUID.randomUUID();
        String outputKey = "outputs/user/frames.zip";
        String body = String.format("{\"videoId\": \"%s\", \"status\": \"DONE\", \"outputKey\": \"%s\"}", videoId, outputKey);

        consumer.consume(body, "video.events", "video.processed");

        verify(updateVideoStatus).execute(videoId, VideoStatus.DONE, outputKey);
    }

    @Test
    @DisplayName("consume normalizes lowercase status")
    void consume_lowercaseStatus_normalizesToUpperCase() {
        UUID videoId = UUID.randomUUID();
        String body = String.format("{\"videoId\": \"%s\", \"status\": \"done\"}", videoId);

        consumer.consume(body, "video.events", "video.processed");

        verify(updateVideoStatus).execute(videoId, VideoStatus.DONE, null);
    }

    @Test
    @DisplayName("consume creates record with full metadata when video not found")
    void consume_videoNotFound_createsOrphanRecordAndAppliesStatus() {
        UUID videoId = UUID.randomUUID();
        String outputKey = "outputs/user-1/frames.zip";
        String storageAbsolutePath = "E:\\uploads\\jonasfs\\video.mp4";
        String body = String.format(
                "{\"videoId\": \"%s\", \"userId\": \"user-1\", \"outputKey\": \"%s\"," +
                " \"originalFilename\": \"video.mp4\", \"fileSizeBytes\": 54048774," +
                " \"mimeType\": \"video/mp4\", \"storageAbsolutePath\": \"%s\", \"status\": \"DONE\"}",
                videoId, outputKey, storageAbsolutePath.replace("\\", "\\\\"));
        Video orphan = Video.createWithId(videoId, "user-1", "video.mp4", 54048774L, "video/mp4", storageAbsolutePath);

        doThrow(new VideoNotFoundException(videoId.toString()))
                .doReturn(orphan)
                .when(updateVideoStatus).execute(videoId, VideoStatus.DONE, outputKey);
        doReturn(orphan)
                .when(registerVideoUploaded).execute(videoId, "user-1", "video.mp4", 54048774L, "video/mp4", storageAbsolutePath);

        consumer.consume(body, "video.events", "video.processed");

        verify(registerVideoUploaded).execute(videoId, "user-1", "video.mp4", 54048774L, "video/mp4", storageAbsolutePath);
        verify(updateVideoStatus, times(2)).execute(videoId, VideoStatus.DONE, outputKey);
    }

    @Test
    @DisplayName("consume orphan falls back to outputKey when storageAbsolutePath absent")
    void consume_videoNotFound_fallsBackToOutputKeyForStorageKey() {
        UUID videoId = UUID.randomUUID();
        String outputKey = "outputs/user-1/frames.zip";
        String body = String.format(
                "{\"videoId\": \"%s\", \"userId\": \"user-1\", \"outputKey\": \"%s\", \"status\": \"DONE\"}",
                videoId, outputKey);
        Video orphan = Video.createWithId(videoId, "user-1", "unknown", 0L, "unknown", outputKey);

        doThrow(new VideoNotFoundException(videoId.toString()))
                .doReturn(orphan)
                .when(updateVideoStatus).execute(videoId, VideoStatus.DONE, outputKey);
        doReturn(orphan)
                .when(registerVideoUploaded).execute(videoId, "user-1", "unknown", 0L, "unknown", outputKey);

        consumer.consume(body, "video.events", "video.processed");

        verify(registerVideoUploaded).execute(videoId, "user-1", "unknown", 0L, "unknown", outputKey);
        verify(updateVideoStatus, times(2)).execute(videoId, VideoStatus.DONE, outputKey);
    }

    @Test
    @DisplayName("consume with invalid payload logs error and skips use case (no re-throw)")
    void consume_invalidPayload_logsErrorAndSkipsUseCase() {
        consumer.consume("not-json", null, null);

        verify(updateVideoStatus, never()).execute(any(), any(), any());
    }

    @Test
    @DisplayName("consume with unknown status logs error and skips update (no re-throw)")
    void consume_unknownStatus_logsErrorAndSkipsUpdate() {
        String payload = String.format("{\"videoId\": \"%s\", \"status\": \"UNKNOWN_STATUS\"}", UUID.randomUUID());

        consumer.consume(payload, null, null);

        verify(updateVideoStatus, never()).execute(any(), any(), any());
    }
}