package br.com.fiap.infrastructure.adapters.entities;

import br.com.fiap.domain.model.Video;
import br.com.fiap.domain.model.VideoStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class VideoEntityTest {

    @Test
    void shouldConvertFromDomainAndBack() {
        UUID id = UUID.randomUUID();
        Video video = Video.createWithId(id, "user-1", "test.mp4", 2048L, "video/mp4", "videos/user-1/uuid/test.mp4");

        VideoEntity entity = new VideoEntity(video);
        Video restored = entity.toDomain();

        assertThat(restored.getId()).isEqualTo(id);
        assertThat(restored.getUserId()).isEqualTo("user-1");
        assertThat(restored.getOriginalFilename()).isEqualTo("test.mp4");
        assertThat(restored.getFileSizeBytes()).isEqualTo(2048L);
        assertThat(restored.getMimeType()).isEqualTo("video/mp4");
        assertThat(restored.getStatus()).isEqualTo(VideoStatus.PENDING);
        assertThat(restored.getStorageKey()).isEqualTo("videos/user-1/uuid/test.mp4");
    }

    @Test
    @DisplayName("onCreate() sets createdAt and updatedAt when null")
    void onCreate_setsTimestampsWhenNull() {
        VideoEntity entity = new VideoEntity();

        entity.onCreate();

        assertThat(entity.getCreatedAt()).isNotNull();
        assertThat(entity.getUpdatedAt()).isNotNull();
    }

    @Test
    @DisplayName("onCreate() does not overwrite existing timestamps")
    void onCreate_doesNotOverwriteExistingTimestamps() {
        VideoEntity entity = new VideoEntity();
        LocalDateTime fixed = LocalDateTime.of(2024, 1, 1, 0, 0);
        entity.setCreatedAt(fixed);
        entity.setUpdatedAt(fixed);

        entity.onCreate();

        assertThat(entity.getCreatedAt()).isEqualTo(fixed);
        assertThat(entity.getUpdatedAt()).isEqualTo(fixed);
    }

    @Test
    @DisplayName("onUpdate() refreshes updatedAt")
    void onUpdate_refreshesUpdatedAt() {
        VideoEntity entity = new VideoEntity();

        entity.onUpdate();

        assertThat(entity.getUpdatedAt()).isNotNull();
    }

    @Test
    @DisplayName("All getters and setters work correctly")
    void allSettersAndGetters_workCorrectly() {
        VideoEntity entity = new VideoEntity();
        UUID id = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();

        entity.setId(id);
        entity.setUserId("user-99");
        entity.setOriginalFilename("clip.mp4");
        entity.setFileSizeBytes(512L);
        entity.setMimeType("video/webm");
        entity.setStatus(VideoStatus.DONE);
        entity.setStorageKey("videos/user-99/clip.mp4");
        entity.setCreatedAt(now);
        entity.setUpdatedAt(now);

        assertThat(entity.getId()).isEqualTo(id);
        assertThat(entity.getUserId()).isEqualTo("user-99");
        assertThat(entity.getOriginalFilename()).isEqualTo("clip.mp4");
        assertThat(entity.getFileSizeBytes()).isEqualTo(512L);
        assertThat(entity.getMimeType()).isEqualTo("video/webm");
        assertThat(entity.getStatus()).isEqualTo(VideoStatus.DONE);
        assertThat(entity.getStorageKey()).isEqualTo("videos/user-99/clip.mp4");
        assertThat(entity.getCreatedAt()).isEqualTo(now);
        assertThat(entity.getUpdatedAt()).isEqualTo(now);
    }
}
