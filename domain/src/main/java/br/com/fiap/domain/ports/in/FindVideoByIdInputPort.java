package br.com.fiap.domain.ports.in;

import br.com.fiap.domain.model.Video;

import java.util.UUID;

public interface FindVideoByIdInputPort {
    Video execute(UUID videoId, String userId);
}
