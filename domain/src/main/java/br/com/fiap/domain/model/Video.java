package br.com.fiap.domain.model;

import java.time.LocalDateTime;
import java.util.UUID;

public class Video {

    private UUID id;
    private String userId;
    private String originalFilename;
    private Long fileSizeBytes;
    private String mimeType;
    private VideoStatus status;
    private String storageKey;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Video() {}

    public Video(UUID id, String userId, String originalFilename,
                 Long fileSizeBytes, String mimeType, VideoStatus status,
                 String storageKey, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.userId = userId;
        this.originalFilename = originalFilename;
        this.fileSizeBytes = fileSizeBytes;
        this.mimeType = mimeType;
        this.status = status;
        this.storageKey = storageKey;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    /**
     * Creates the local projection of a video that was just registered via the
     * {@code video-uploaded} SQS event. The id is the one generated upstream by
     * the video-upload-service, so it must be reused here (not regenerated) to
     * keep both services' records correlated.
     */
    public static Video createWithId(UUID id, String userId, String originalFilename,
                                      Long fileSizeBytes, String mimeType, String storageKey) {
        Video v = new Video();
        v.id = id;
        v.userId = userId;
        v.originalFilename = originalFilename;
        v.fileSizeBytes = fileSizeBytes;
        v.mimeType = mimeType;
        v.storageKey = storageKey;
        v.status = VideoStatus.PENDING;
        v.createdAt = LocalDateTime.now();
        v.updatedAt = LocalDateTime.now();
        return v;
    }

    public UUID getId() { return id; }
    public String getUserId() { return userId; }
    public String getOriginalFilename() { return originalFilename; }
    public Long getFileSizeBytes() { return fileSizeBytes; }
    public String getMimeType() { return mimeType; }
    public VideoStatus getStatus() { return status; }
    public String getStorageKey() { return storageKey; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }

    public void setId(UUID id) { this.id = id; }
    public void setUserId(String userId) { this.userId = userId; }
    public void setOriginalFilename(String originalFilename) { this.originalFilename = originalFilename; }
    public void setFileSizeBytes(Long fileSizeBytes) { this.fileSizeBytes = fileSizeBytes; }
    public void setMimeType(String mimeType) { this.mimeType = mimeType; }
    public void setStatus(VideoStatus status) { this.status = status; }
    public void setStorageKey(String storageKey) { this.storageKey = storageKey; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
