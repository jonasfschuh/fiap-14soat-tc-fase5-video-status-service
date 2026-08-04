package br.com.fiap.domain.unit.exceptions;

import br.com.fiap.domain.exceptions.*;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DomainExceptionsTest {

    @Test
    void videoNotFoundException_shouldContainVideoId() {
        VideoNotFoundException ex = new VideoNotFoundException("abc-123");
        assertThat(ex.getMessage()).contains("abc-123");
    }

    @Test
    void videoAccessDeniedException_shouldContainVideoId() {
        VideoAccessDeniedException ex = new VideoAccessDeniedException("abc-123");
        assertThat(ex.getMessage()).contains("abc-123");
    }
}
