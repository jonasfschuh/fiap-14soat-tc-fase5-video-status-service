package br.com.fiap.domain.unit.usecases;

import br.com.fiap.domain.model.Video;
import br.com.fiap.domain.model.VideoStatus;
import br.com.fiap.domain.ports.out.VideoRepositoryPort;
import br.com.fiap.domain.usecases.RegisterVideoUploadedUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RegisterVideoUploadedUseCaseTest {

    @Mock private VideoRepositoryPort repository;
    private RegisterVideoUploadedUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new RegisterVideoUploadedUseCase(repository);
    }

    @Test
    void shouldRegisterNewVideoWhenNotYetKnown() {
        UUID id = UUID.randomUUID();
        when(repository.findById(id)).thenReturn(Optional.empty());
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Video result = useCase.execute(id, "user-1", "video.mp4", 1024L, "video/mp4", "videos/user-1/video.mp4");

        assertThat(result.getId()).isEqualTo(id);
        assertThat(result.getStatus()).isEqualTo(VideoStatus.PENDING);
        assertThat(result.getUserId()).isEqualTo("user-1");
        verify(repository).save(any());
    }

    @Test
    void shouldBeIdempotentWhenVideoAlreadyRegistered() {
        UUID id = UUID.randomUUID();
        Video existing = Video.createWithId(id, "user-1", "video.mp4", 1024L, "video/mp4", "videos/user-1/video.mp4");
        existing.setStatus(VideoStatus.PROCESSING);
        when(repository.findById(id)).thenReturn(Optional.of(existing));

        Video result = useCase.execute(id, "user-1", "video.mp4", 1024L, "video/mp4", "videos/user-1/video.mp4");

        assertThat(result.getStatus()).isEqualTo(VideoStatus.PROCESSING);
        verify(repository, never()).save(any());
    }
}
