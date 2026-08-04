package br.com.fiap.domain.unit.usecases;

import br.com.fiap.domain.exceptions.VideoNotFoundException;
import br.com.fiap.domain.model.Video;
import br.com.fiap.domain.model.VideoStatus;
import br.com.fiap.domain.ports.out.VideoRepositoryPort;
import br.com.fiap.domain.usecases.UpdateVideoStatusUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UpdateVideoStatusUseCaseTest {

    @Mock private VideoRepositoryPort repository;
    private UpdateVideoStatusUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new UpdateVideoStatusUseCase(repository);
    }

    @Test
    void shouldUpdateStatusOfExistingVideo() {
        UUID id = UUID.randomUUID();
        Video video = Video.createWithId(id, "user-1", "video.mp4", 1024L, "video/mp4", "videos/user-1/video.mp4");
        when(repository.findById(id)).thenReturn(Optional.of(video));
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Video result = useCase.execute(id, VideoStatus.DONE);

        assertThat(result.getStatus()).isEqualTo(VideoStatus.DONE);
    }

    @Test
    void shouldThrowNotFoundWhenVideoDoesNotExist() {
        UUID id = UUID.randomUUID();
        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(id, VideoStatus.FAILED))
                .isInstanceOf(VideoNotFoundException.class);
    }
}
