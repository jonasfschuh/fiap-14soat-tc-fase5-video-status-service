package br.com.fiap.infrastructure.configuration;

import io.swagger.v3.oas.models.OpenAPI;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("SwaggerConfiguration - Unit Tests")
class SwaggerConfigurationTest {

    private SwaggerConfiguration config;

    @BeforeEach
    void setUp() {
        config = new SwaggerConfiguration();
    }

    @Test
    @DisplayName("customOpenAPI returns local status service server")
    void customOpenAPI_returnsLocalServer() {
        OpenAPI openApi = config.customOpenAPI();

        assertThat(openApi).isNotNull();
        assertThat(openApi.getInfo()).isNotNull();
        assertThat(openApi.getInfo().getTitle()).contains("Video Status");
        assertThat(openApi.getServers()).isNotEmpty();
        assertThat(openApi.getServers().get(0).getUrl()).isEqualTo("/");
    }

    @Test
    @DisplayName("customOpenAPI has only one server so Swagger routes via proxy (no CORS)")
    void customOpenAPI_hasOnlyOneServer() {
        OpenAPI openApi = config.customOpenAPI();

        assertThat(openApi.getServers()).hasSize(1);
        assertThat(openApi.getServers().get(0).getUrl()).isEqualTo("/");
    }

    @Test
    @DisplayName("customOpenAPI has security scheme configured")
    void customOpenAPI_hasSecurityScheme() {
        OpenAPI openApi = config.customOpenAPI();

        assertThat(openApi.getComponents()).isNotNull();
        assertThat(openApi.getComponents().getSecuritySchemes()).containsKey("bearer-jwt");
    }
}