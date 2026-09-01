package br.com.fiap.infrastructure.configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

@Configuration
public class SwaggerConfiguration {

    /**
     * Explicit server URL shown in Swagger UI (e.g. http://localhost/status for K8s ingress).
     * Defaults to "/" (relative) so Swagger calls go to the same host/port it was opened from.
     */
    @Value("${swagger.server.url:/}")
    private String swaggerServerUrl;

    @Bean
    public OpenAPI customOpenAPI() {
        String serverUrl = StringUtils.hasText(swaggerServerUrl) ? swaggerServerUrl : "/";
        return new OpenAPI()
                .servers(java.util.List.of(
                        new Server().url(serverUrl).description("API Server")
                ))
                .components(new Components()
                        .addSecuritySchemes("bearer-jwt", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("JWT obtido via POST /auth/login. Informe: ******")
                        )
                )
                .addSecurityItem(new SecurityRequirement().addList("bearer-jwt"))
                .info(new Info()
                        .title("FIAP X - Video Status MS")
                        .version("1.0.0")
                        .description("""
                                FIAP - 14 SOAT - Arquitetura de Software (Turma Outubro de 2025)
                                Tech Challenge - Fase 5 (Hackathon)

                                **Como autenticar:**
                                1. Expanda a secao **Authentication** abaixo
                                2. Execute `POST /auth/login` com suas credenciais
                                3. Copie o `token` da resposta
                                4. Clique em **Authorize** (\uD83D\uDD12) e informe: `******
                                """));
    }
}