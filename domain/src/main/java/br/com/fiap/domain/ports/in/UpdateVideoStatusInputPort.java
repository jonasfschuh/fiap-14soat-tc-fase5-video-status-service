package br.com.fiap.domain.ports.in;

import br.com.fiap.domain.model.Video;
import br.com.fiap.domain.model.VideoStatus;

import java.util.UUID;

/**
 * Handles {@code video-events} SQS messages, updating the status of an
 * already registered video (e.g. PROCESSING, DONE, FAILED).
 */
public interface UpdateVideoStatusInputPort {
    Video execute(UUID videoId, VideoStatus status);
}
