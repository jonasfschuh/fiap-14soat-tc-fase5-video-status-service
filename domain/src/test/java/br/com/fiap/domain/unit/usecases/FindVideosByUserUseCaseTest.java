package br.com.fiap.domain.unit.usecases;

import br.com.fiap.domain.model.Video;
import br.com.fiap.domain.ports.out.VideoRepositoryPort;
import br.com.fiap.domain.usecases.FindVideosByUserUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FindVideosByUserUseCaseTest {

    @Mock private VideoRepositoryPort repository;
    private FindVideosByUserUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new FindVideosByUserUseCase(repository);
    }

    @Test
    void shouldReturnVideosForUser() {
        Video video = Video.createWithId(UUID.randomUUID(), "user-123", "test.mp4", 1024L, "video/mp4", "videos/user-123/test.mp4");
        when(repository.findByUserId("user-123")).thenReturn(List.of(video));

        List<Video> result = useCase.execute("user-123");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getUserId()).isEqualTo("user-123");
    }

    @Test
    void shouldReturnEmptyListWhenNoVideos() {
        when(repository.findByUserId("user-123")).thenReturn(List.of());
        List<Video> result = useCase.execute("user-123");
        assertThat(result).isEmpty();
    }

    @Test
    void shouldReturnAllVideosWhenUserIdIsNull() {
        Video v1 = Video.createWithId(UUID.randomUUID(), "user-1", "a.mp4", 1024L, "video/mp4", "key1");
        Video v2 = Video.createWithId(UUID.randomUUID(), "user-2", "b.mp4", 2048L, "video/mp4", "key2");
        when(repository.findAll()).thenReturn(List.of(v1, v2));

        List<Video> result = useCase.execute(null);

        assertThat(result).hasSize(2);
    }

    @Test
    void shouldReturnAllVideosWhenUserIdIsBlank() {
        Video v1 = Video.createWithId(UUID.randomUUID(), "user-1", "a.mp4", 1024L, "video/mp4", "key1");
        when(repository.findAll()).thenReturn(List.of(v1));

        List<Video> result = useCase.execute("   ");

        assertThat(result).hasSize(1);
    }
}
