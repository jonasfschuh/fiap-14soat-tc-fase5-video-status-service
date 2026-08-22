package br.com.fiap.infrastructure.configuration;

import io.swagger.v3.oas.models.OpenAPI;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

@DisplayName("SwaggerConfiguration - Unit Tests")
class SwaggerConfigurationTest {

    private SwaggerConfiguration config;

    @BeforeEach
    void setUp() {
        config = new SwaggerConfiguration();
    }

    @Test
    @DisplayName("customOpenAPI returns local status service server")
    void customOpenAPI_withoutAuthUrl_returnsLocalServers() {
        OpenAPI openApi = config.customOpenAPI();

        assertThat(openApi).isNotNull();
        assertThat(openApi.getInfo()).isNotNull();
        assertThat(openApi.getInfo().getTitle()).contains("Video Status");
        assertThat(openApi.getServers()).isNotEmpty();
        assertThat(openApi.getServers().get(0).getUrl()).isEqualTo("/");
    }

    @Test
    @DisplayName("customOpenAPI keeps status service server even when auth URL is configured")
    void customOpenAPI_withAuthUrl_keepsLocalServer() {
        ReflectionTestUtils.setField(config, "authServiceUrl", "https://auth.example.com");

        OpenAPI openApi = config.customOpenAPI();

        assertThat(openApi).isNotNull();
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

    @Test
    @DisplayName("authLoginServerOverride customizer does not throw when auth URL is empty")
    void authLoginServerOverride_withoutUrl_doesNotThrow() {
        ReflectionTestUtils.setField(config, "authServiceUrl", "");
        var customizer = config.authLoginServerOverride();

        OpenAPI openApi = config.customOpenAPI();
        assertThatCode(() -> customizer.customise(openApi)).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("authLoginServerOverride customizer handles null paths gracefully")
    void authLoginServerOverride_withNullPaths_doesNotThrow() {
        ReflectionTestUtils.setField(config, "authServiceUrl", "https://auth.example.com");
        var customizer = config.authLoginServerOverride();

        OpenAPI openApi = new OpenAPI();
        openApi.setPaths(null);
        assertThatCode(() -> customizer.customise(openApi)).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("authLoginServerOverride customizer overrides auth login path server when URL is set")
    void authLoginServerOverride_withUrl_overridesLoginServer() {
        ReflectionTestUtils.setField(config, "authServiceUrl", "https://auth.example.com");
        var customizer = config.authLoginServerOverride();

        OpenAPI openApi = config.customOpenAPI();
        io.swagger.v3.oas.models.Paths paths = new io.swagger.v3.oas.models.Paths();
        paths.addPathItem("/auth/login", new io.swagger.v3.oas.models.PathItem());
        openApi.setPaths(paths);

        assertThatCode(() -> customizer.customise(openApi)).doesNotThrowAnyException();
        assertThat(openApi.getPaths().get("/auth/login").getServers()).hasSize(1);
        assertThat(openApi.getPaths().get("/auth/login").getServers().get(0).getUrl()).isEqualTo("https://auth.example.com");
    }
}
