package br.com.fiap.domain.usecases;

import br.com.fiap.domain.model.Video;
import br.com.fiap.domain.ports.in.FindVideosByUserInputPort;
import br.com.fiap.domain.ports.out.VideoRepositoryPort;

import java.util.List;

public class FindVideosByUserUseCase implements FindVideosByUserInputPort {

    private final VideoRepositoryPort repository;

    public FindVideosByUserUseCase(VideoRepositoryPort repository) {
        this.repository = repository;
    }

    @Override
    public List<Video> execute(String userId) {
        return repository.findByUserId(userId);
    }
}
