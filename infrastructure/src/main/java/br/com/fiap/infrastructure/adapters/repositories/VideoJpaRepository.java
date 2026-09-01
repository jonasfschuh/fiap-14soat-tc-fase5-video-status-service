package br.com.fiap.infrastructure.adapters.repositories;

import br.com.fiap.infrastructure.adapters.entities.VideoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface VideoJpaRepository extends JpaRepository<VideoEntity, UUID> {
    List<VideoEntity> findByUserId(String userId);
    Optional<VideoEntity> findByIdAndUserId(UUID id, String userId);
    List<VideoEntity> findByUserIdOrderByCreatedAtDesc(String userId);
    List<VideoEntity> findAllByOrderByCreatedAtDesc();
}
