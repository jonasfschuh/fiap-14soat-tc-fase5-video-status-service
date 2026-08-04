package br.com.fiap.domain.unit.usecases;

import br.com.fiap.domain.exceptions.VideoAccessDeniedException;
import br.com.fiap.domain.exceptions.VideoNotFoundException;
import br.com.fiap.domain.model.Video;
import br.com.fiap.domain.ports.out.VideoRepositoryPort;
import br.com.fiap.domain.usecases.FindVideoByIdUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FindVideoByIdUseCaseTest {

    @Mock private VideoRepositoryPort repository;
    private FindVideoByIdUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new FindVideoByIdUseCase(repository);
    }

    @Test
    void shouldReturnVideoWhenFoundAndOwned() {
        UUID id = UUID.randomUUID();
        Video video = Video.createWithId(id, "user-123", "test.mp4", 1024L, "video/mp4", "videos/user-123/test.mp4");
        when(repository.findById(id)).thenReturn(Optional.of(video));

        Video result = useCase.execute(id, "user-123");

        assertThat(result.getId()).isEqualTo(id);
    }

    @Test
    void shouldThrowNotFoundWhenVideoDoesNotExist() {
        UUID id = UUID.randomUUID();
        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(id, "user-123"))
                .isInstanceOf(VideoNotFoundException.class);
    }

    @Test
    void shouldThrowAccessDeniedWhenNotOwner() {
        UUID id = UUID.randomUUID();
        Video video = Video.createWithId(id, "user-123", "test.mp4", 1024L, "video/mp4", "videos/user-123/test.mp4");
        when(repository.findById(id)).thenReturn(Optional.of(video));

        assertThatThrownBy(() -> useCase.execute(id, "other-user"))
                .isInstanceOf(VideoAccessDeniedException.class);
    }
}
