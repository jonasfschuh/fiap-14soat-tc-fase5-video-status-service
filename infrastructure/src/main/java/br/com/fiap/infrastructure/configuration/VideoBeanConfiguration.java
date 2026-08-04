package br.com.fiap.infrastructure.configuration;

import br.com.fiap.domain.ports.in.FindVideoByIdInputPort;
import br.com.fiap.domain.ports.in.FindVideosByUserInputPort;
import br.com.fiap.domain.ports.in.RegisterVideoUploadedInputPort;
import br.com.fiap.domain.ports.in.UpdateVideoStatusInputPort;
import br.com.fiap.domain.ports.out.VideoRepositoryPort;
import br.com.fiap.domain.usecases.FindVideoByIdUseCase;
import br.com.fiap.domain.usecases.FindVideosByUserUseCase;
import br.com.fiap.domain.usecases.RegisterVideoUploadedUseCase;
import br.com.fiap.domain.usecases.UpdateVideoStatusUseCase;
import br.com.fiap.infrastructure.adapters.repositories.VideoJpaRepository;
import br.com.fiap.infrastructure.adapters.repositories.VideoRepositoryImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class VideoBeanConfiguration {

    @Bean
    public VideoRepositoryPort videoRepositoryPort(VideoJpaRepository jpaRepository) {
        return new VideoRepositoryImpl(jpaRepository);
    }

    @Bean
    public FindVideosByUserInputPort findVideosByUserInputPort(VideoRepositoryPort repository) {
        return new FindVideosByUserUseCase(repository);
    }

    @Bean
    public FindVideoByIdInputPort findVideoByIdInputPort(VideoRepositoryPort repository) {
        return new FindVideoByIdUseCase(repository);
    }

    @Bean
    public RegisterVideoUploadedInputPort registerVideoUploadedInputPort(VideoRepositoryPort repository) {
        return new RegisterVideoUploadedUseCase(repository);
    }

    @Bean
    public UpdateVideoStatusInputPort updateVideoStatusInputPort(VideoRepositoryPort repository) {
        return new UpdateVideoStatusUseCase(repository);
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
