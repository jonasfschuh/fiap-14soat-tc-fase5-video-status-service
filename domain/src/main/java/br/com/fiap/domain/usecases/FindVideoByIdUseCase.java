package br.com.fiap.domain.usecases;

import br.com.fiap.domain.exceptions.VideoAccessDeniedException;
import br.com.fiap.domain.exceptions.VideoNotFoundException;
import br.com.fiap.domain.model.Video;
import br.com.fiap.domain.ports.in.FindVideoByIdInputPort;
import br.com.fiap.domain.ports.out.VideoRepositoryPort;

import java.util.UUID;

public class FindVideoByIdUseCase implements FindVideoByIdInputPort {

    private final VideoRepositoryPort repository;

    public FindVideoByIdUseCase(VideoRepositoryPort repository) {
        this.repository = repository;
    }

    @Override
    public Video execute(UUID videoId, String userId) {
        Video video = repository.findById(videoId)
                .orElseThrow(() -> new VideoNotFoundException(videoId.toString()));

        if (userId != null && !video.getUserId().equals(userId)) {
            throw new VideoAccessDeniedException(videoId.toString());
        }

        return video;
    }
}
