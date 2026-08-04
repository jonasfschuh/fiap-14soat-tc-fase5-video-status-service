package br.com.fiap.infrastructure.adapters.repositories;

import br.com.fiap.domain.model.Video;
import br.com.fiap.infrastructure.adapters.entities.VideoEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("VideoRepositoryImpl - Unit Tests")
class VideoRepositoryImplTest {

    @Mock private VideoJpaRepository jpaRepository;

    private VideoRepositoryImpl repository;

    @BeforeEach
    void setUp() {
        repository = new VideoRepositoryImpl(jpaRepository);
    }

    @Test
    @DisplayName("save converts domain to entity, persists, and returns domain video")
    void save_persistsVideoAndReturnsDomain() {
        Video video = Video.createWithId(UUID.randomUUID(), "user-1", "test.mp4", 1024L, "video/mp4", "videos/user-1/test.mp4");
        VideoEntity entity = new VideoEntity(video);
        when(jpaRepository.save(any(VideoEntity.class))).thenReturn(entity);

        Video result = repository.save(video);

        assertThat(result.getUserId()).isEqualTo("user-1");
        assertThat(result.getOriginalFilename()).isEqualTo("test.mp4");
        verify(jpaRepository).save(any(VideoEntity.class));
    }

    @Test
    @DisplayName("findById returns domain video when entity exists")
    void findById_whenPresent_returnsDomainVideo() {
        Video video = Video.createWithId(UUID.randomUUID(), "user-1", "test.mp4", 1024L, "video/mp4", "videos/user-1/test.mp4");
        VideoEntity entity = new VideoEntity(video);
        when(jpaRepository.findById(video.getId())).thenReturn(Optional.of(entity));

        Optional<Video> result = repository.findById(video.getId());

        assertThat(result).isPresent();
        assertThat(result.get().getUserId()).isEqualTo("user-1");
    }

    @Test
    @DisplayName("findById returns empty when entity not found")
    void findById_whenAbsent_returnsEmpty() {
        UUID id = UUID.randomUUID();
        when(jpaRepository.findById(id)).thenReturn(Optional.empty());

        assertThat(repository.findById(id)).isEmpty();
    }

    @Test
    @DisplayName("findByIdAndUserId returns domain video when found")
    void findByIdAndUserId_whenPresent_returnsDomainVideo() {
        Video video = Video.createWithId(UUID.randomUUID(), "user-1", "test.mp4", 1024L, "video/mp4", "videos/user-1/test.mp4");
        VideoEntity entity = new VideoEntity(video);
        when(jpaRepository.findByIdAndUserId(video.getId(), "user-1")).thenReturn(Optional.of(entity));

        Optional<Video> result = repository.findByIdAndUserId(video.getId(), "user-1");

        assertThat(result).isPresent();
    }

    @Test
    @DisplayName("findByIdAndUserId returns empty when not found")
    void findByIdAndUserId_whenAbsent_returnsEmpty() {
        UUID id = UUID.randomUUID();
        when(jpaRepository.findByIdAndUserId(id, "user-1")).thenReturn(Optional.empty());

        assertThat(repository.findByIdAndUserId(id, "user-1")).isEmpty();
    }

    @Test
    @DisplayName("findByUserId returns list of domain videos")
    void findByUserId_returnsMappedList() {
        Video video = Video.createWithId(UUID.randomUUID(), "user-1", "test.mp4", 1024L, "video/mp4", "videos/user-1/test.mp4");
        VideoEntity entity = new VideoEntity(video);
        when(jpaRepository.findByUserId("user-1")).thenReturn(List.of(entity));

        List<Video> result = repository.findByUserId("user-1");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getUserId()).isEqualTo("user-1");
    }

    @Test
    @DisplayName("findByUserId returns empty list when no videos found")
    void findByUserId_whenNone_returnsEmptyList() {
        when(jpaRepository.findByUserId("user-x")).thenReturn(List.of());

        assertThat(repository.findByUserId("user-x")).isEmpty();
    }
}
