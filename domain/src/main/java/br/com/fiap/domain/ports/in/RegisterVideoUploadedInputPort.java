package br.com.fiap.domain.ports.in;

import br.com.fiap.domain.model.Video;

import java.util.UUID;

/**
 * Handles the {@code video-uploaded} SQS event, registering the initial
 * status projection (PENDING) for a video that was just uploaded.
 */
public interface RegisterVideoUploadedInputPort {
    Video execute(UUID videoId, String userId, String originalFilename,
                  Long fileSizeBytes, String mimeType, String storageKey);
}
