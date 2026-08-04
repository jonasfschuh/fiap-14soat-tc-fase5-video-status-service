package br.com.fiap.domain.usecases;

import br.com.fiap.domain.model.Video;
import br.com.fiap.domain.ports.in.RegisterVideoUploadedInputPort;
import br.com.fiap.domain.ports.out.VideoRepositoryPort;

import java.util.UUID;

public class RegisterVideoUploadedUseCase implements RegisterVideoUploadedInputPort {

    private final VideoRepositoryPort repository;

    public RegisterVideoUploadedUseCase(VideoRepositoryPort repository) {
        this.repository = repository;
    }

    @Override
    public Video execute(UUID videoId, String userId, String originalFilename,
                          Long fileSizeBytes, String mimeType, String storageKey) {
        // Idempotent: SQS may redeliver the same video-uploaded message more than
        // once. If a projection already exists for this videoId, keep it as-is
        // (it might already have been advanced by a video-events message).
        return repository.findById(videoId)
                .orElseGet(() -> repository.save(
                        Video.createWithId(videoId, userId, originalFilename, fileSizeBytes, mimeType, storageKey)));
    }
}
