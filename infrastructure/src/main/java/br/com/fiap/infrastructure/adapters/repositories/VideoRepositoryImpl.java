package br.com.fiap.infrastructure.adapters.repositories;

import br.com.fiap.domain.model.Video;
import br.com.fiap.domain.ports.out.VideoRepositoryPort;
import br.com.fiap.infrastructure.adapters.entities.VideoEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class VideoRepositoryImpl implements VideoRepositoryPort {

    private final VideoJpaRepository jpaRepository;

    public VideoRepositoryImpl(VideoJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Video save(Video video) {
        VideoEntity entity = new VideoEntity(video);
        return jpaRepository.save(entity).toDomain();
    }

    @Override
    public Optional<Video> findById(UUID id) {
        return jpaRepository.findById(id).map(VideoEntity::toDomain);
    }

    @Override
    public Optional<Video> findByIdAndUserId(UUID id, String userId) {
        return jpaRepository.findByIdAndUserId(id, userId).map(VideoEntity::toDomain);
    }

    @Override
    public List<Video> findByUserId(String userId) {
        return jpaRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(VideoEntity::toDomain)
                .toList();
    }

    @Override
    public List<Video> findAll() {
        return jpaRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(VideoEntity::toDomain)
                .toList();
    }
}
