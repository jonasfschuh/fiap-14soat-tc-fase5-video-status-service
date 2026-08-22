package br.com.fiap.domain.ports.out;

import br.com.fiap.domain.model.Video;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface VideoRepositoryPort {
    Video save(Video video);
    Optional<Video> findById(UUID id);
    Optional<Video> findByIdAndUserId(UUID id, String userId);
    List<Video> findByUserId(String userId);
    List<Video> findAll();
}
