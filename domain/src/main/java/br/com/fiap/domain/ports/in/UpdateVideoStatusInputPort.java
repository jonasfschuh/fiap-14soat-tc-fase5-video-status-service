package br.com.fiap.domain.ports.in;

import br.com.fiap.domain.model.Video;
import br.com.fiap.domain.model.VideoStatus;

import java.util.UUID;

/**
 * Handles {@code video-events} messages, updating the status of an already
 * registered video (e.g. PROCESSING, DONE, FAILED). When {@code outputKey} is
 * provided (non-null / non-blank), the video's storageKey is also updated so
 * that downstream services can locate the processed output.
 */
public interface UpdateVideoStatusInputPort {

    Video execute(UUID videoId, VideoStatus status, String outputKey);

    default Video execute(UUID videoId, VideoStatus status) {
        return execute(videoId, status, null);
    }
}
