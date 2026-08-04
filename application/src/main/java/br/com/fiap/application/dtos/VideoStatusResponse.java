package br.com.fiap.application.dtos;

import java.time.LocalDateTime;
import java.util.UUID;

public record VideoStatusResponse(
        UUID videoId,
        String userId,
        String originalFilename,
        Long fileSizeBytes,
        String mimeType,
        String status,
        String storageKey,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
