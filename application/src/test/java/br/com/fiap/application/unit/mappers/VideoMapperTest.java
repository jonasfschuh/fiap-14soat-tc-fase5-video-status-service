package br.com.fiap.application.unit.mappers;

import br.com.fiap.application.dtos.VideoStatusResponse;
import br.com.fiap.application.mappers.VideoMapper;
import br.com.fiap.domain.model.Video;
import br.com.fiap.domain.model.VideoStatus;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class VideoMapperTest {

    @Test
    void toStatusResponse_shouldMapAllFields() {
        UUID id = UUID.randomUUID();
        Video video = Video.createWithId(id, "user-1", "video.mp4", 2048L, "video/mp4", "videos/user-1/uuid/video.mp4");
        video.setStatus(VideoStatus.PROCESSING);

        VideoStatusResponse response = VideoMapper.toStatusResponse(video);

        assertThat(response.videoId()).isEqualTo(id);
        assertThat(response.userId()).isEqualTo("user-1");
        assertThat(response.originalFilename()).isEqualTo("video.mp4");
        assertThat(response.fileSizeBytes()).isEqualTo(2048L);
        assertThat(response.mimeType()).isEqualTo("video/mp4");
        assertThat(response.status()).isEqualTo(VideoStatus.PROCESSING.name());
        assertThat(response.storageKey()).isEqualTo("videos/user-1/uuid/video.mp4");
        assertThat(response.createdAt()).isNotNull();
        assertThat(response.updatedAt()).isNotNull();
    }
}
