package br.com.fiap.application.mappers;

import br.com.fiap.application.dtos.VideoStatusResponse;
import br.com.fiap.domain.model.Video;

public class VideoMapper {

    private VideoMapper() {}

    public static VideoStatusResponse toStatusResponse(Video video) {
        return new VideoStatusResponse(
                video.getId(),
                video.getUserId(),
                video.getOriginalFilename(),
                video.getFileSizeBytes(),
                video.getMimeType(),
                video.getStatus().name(),
                video.getStorageKey(),
                video.getCreatedAt(),
                video.getUpdatedAt()
        );
    }
}
