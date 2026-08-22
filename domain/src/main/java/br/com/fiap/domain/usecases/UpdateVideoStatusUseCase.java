package br.com.fiap.domain.usecases;

import br.com.fiap.domain.exceptions.VideoNotFoundException;
import br.com.fiap.domain.model.Video;
import br.com.fiap.domain.model.VideoStatus;
import br.com.fiap.domain.ports.in.UpdateVideoStatusInputPort;
import br.com.fiap.domain.ports.out.VideoRepositoryPort;

import java.time.LocalDateTime;
import java.util.UUID;

public class UpdateVideoStatusUseCase implements UpdateVideoStatusInputPort {

    private final VideoRepositoryPort repository;

    public UpdateVideoStatusUseCase(VideoRepositoryPort repository) {
        this.repository = repository;
    }

    @Override
    public Video execute(UUID videoId, VideoStatus status, String outputKey) {
        Video video = repository.findById(videoId)
                .orElseThrow(() -> new VideoNotFoundException(videoId.toString()));

        video.setStatus(status);
        if (outputKey != null && !outputKey.isBlank()) {
            video.setStorageKey(outputKey);
        }
        video.setUpdatedAt(LocalDateTime.now());

        return repository.save(video);
    }
}
