package br.com.fiap.domain.unit.model;

import br.com.fiap.domain.model.Video;
import br.com.fiap.domain.model.VideoStatus;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class VideoModelTest {

    @Test
    void shouldCreateVideoWithIdAndPendingStatus() {
        UUID id = UUID.randomUUID();
        Video video = Video.createWithId(id, "user-123", "video.mp4", 10485760L, "video/mp4", "videos/user-123/video.mp4");

        assertThat(video.getId()).isEqualTo(id);
        assertThat(video.getUserId()).isEqualTo("user-123");
        assertThat(video.getOriginalFilename()).isEqualTo("video.mp4");
        assertThat(video.getFileSizeBytes()).isEqualTo(10485760L);
        assertThat(video.getMimeType()).isEqualTo("video/mp4");
        assertThat(video.getStorageKey()).isEqualTo("videos/user-123/video.mp4");
        assertThat(video.getStatus()).isEqualTo(VideoStatus.PENDING);
        assertThat(video.getCreatedAt()).isNotNull();
        assertThat(video.getUpdatedAt()).isNotNull();
    }

    @Test
    void shouldBuildVideoViaAllArgsConstructor() {
        UUID id = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();

        Video video = new Video(id, "user-1", "clip.mp4", 2048L, "video/mp4",
                VideoStatus.DONE, "videos/user-1/clip.mp4", now, now);

        assertThat(video.getId()).isEqualTo(id);
        assertThat(video.getUserId()).isEqualTo("user-1");
        assertThat(video.getOriginalFilename()).isEqualTo("clip.mp4");
        assertThat(video.getFileSizeBytes()).isEqualTo(2048L);
        assertThat(video.getMimeType()).isEqualTo("video/mp4");
        assertThat(video.getStatus()).isEqualTo(VideoStatus.DONE);
        assertThat(video.getStorageKey()).isEqualTo("videos/user-1/clip.mp4");
        assertThat(video.getCreatedAt()).isEqualTo(now);
        assertThat(video.getUpdatedAt()).isEqualTo(now);
    }

    @Test
    void shouldUpdateAllFieldsViaSetters() {
        Video video = new Video();
        UUID id = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();

        video.setId(id);
        video.setUserId("user-99");
        video.setOriginalFilename("updated.mp4");
        video.setFileSizeBytes(512L);
        video.setMimeType("video/webm");
        video.setStatus(VideoStatus.PROCESSING);
        video.setStorageKey("videos/user-99/updated.mp4");
        video.setCreatedAt(now);
        video.setUpdatedAt(now);

        assertThat(video.getId()).isEqualTo(id);
        assertThat(video.getUserId()).isEqualTo("user-99");
        assertThat(video.getOriginalFilename()).isEqualTo("updated.mp4");
        assertThat(video.getFileSizeBytes()).isEqualTo(512L);
        assertThat(video.getMimeType()).isEqualTo("video/webm");
        assertThat(video.getStatus()).isEqualTo(VideoStatus.PROCESSING);
        assertThat(video.getStorageKey()).isEqualTo("videos/user-99/updated.mp4");
        assertThat(video.getCreatedAt()).isEqualTo(now);
        assertThat(video.getUpdatedAt()).isEqualTo(now);
    }
}
