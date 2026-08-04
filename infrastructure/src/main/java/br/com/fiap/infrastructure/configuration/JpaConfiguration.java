package br.com.fiap.infrastructure.configuration;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(basePackages = "br.com.fiap.infrastructure.adapters.repositories")
@EntityScan(basePackages = "br.com.fiap.infrastructure.adapters.entities")
public class JpaConfiguration {
}
