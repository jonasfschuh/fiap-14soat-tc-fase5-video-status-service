package br.com.fiap.infrastructure.configuration;

import br.com.fiap.domain.ports.in.FindVideoByIdInputPort;
import br.com.fiap.domain.ports.in.FindVideosByUserInputPort;
import br.com.fiap.domain.ports.in.RegisterVideoUploadedInputPort;
import br.com.fiap.domain.ports.in.UpdateVideoStatusInputPort;
import br.com.fiap.domain.ports.out.VideoRepositoryPort;
import br.com.fiap.infrastructure.adapters.repositories.VideoJpaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class VideoBeanConfigurationTest {

    @Mock private VideoJpaRepository jpaRepository;

    @Test
    void shouldCreateAllBeans() {
        VideoBeanConfiguration config = new VideoBeanConfiguration();

        VideoRepositoryPort repo = config.videoRepositoryPort(jpaRepository);
        assertThat(repo).isNotNull();

        FindVideosByUserInputPort findByUser = config.findVideosByUserInputPort(repo);
        assertThat(findByUser).isNotNull();

        FindVideoByIdInputPort findById = config.findVideoByIdInputPort(repo);
        assertThat(findById).isNotNull();

        RegisterVideoUploadedInputPort registerVideoUploaded = config.registerVideoUploadedInputPort(repo);
        assertThat(registerVideoUploaded).isNotNull();

        UpdateVideoStatusInputPort updateVideoStatus = config.updateVideoStatusInputPort(repo);
        assertThat(updateVideoStatus).isNotNull();

        RestTemplate restTemplate = config.restTemplate();
        assertThat(restTemplate).isNotNull();
    }
}
