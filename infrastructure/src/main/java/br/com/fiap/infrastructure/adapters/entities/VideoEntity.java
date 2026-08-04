package br.com.fiap.infrastructure.adapters.entities;

import br.com.fiap.domain.model.Video;
import br.com.fiap.domain.model.VideoStatus;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * JPA projection of a video's status. The id is always assigned upstream
 * (received via the video-uploaded SQS event) and never database-generated,
 * so Spring Data JPA treats a non-null id as "not new" and calls
 * {@code EntityManager.merge}, which transparently inserts or updates the
 * row depending on whether it already exists. This lets the same
 * {@code save} operation both register a new video and apply status
 * updates coming from the video-events queue.
 */
@Entity
@Table(name = "videos")
public class VideoEntity {

    @Id
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "user_id", nullable = false, length = 255)
    private String userId;

    @Column(name = "original_filename", nullable = false, length = 500)
    private String originalFilename;

    @Column(name = "file_size_bytes", nullable = false)
    private Long fileSizeBytes;

    @Column(name = "mime_type", nullable = false, length = 100)
    private String mimeType;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 50)
    private VideoStatus status;

    @Column(name = "storage_key")
    private String storageKey;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public VideoEntity() {}

    public VideoEntity(Video video) {
        this.id = video.getId();
        this.userId = video.getUserId();
        this.originalFilename = video.getOriginalFilename();
        this.fileSizeBytes = video.getFileSizeBytes();
        this.mimeType = video.getMimeType();
        this.status = video.getStatus();
        this.storageKey = video.getStorageKey();
        this.createdAt = video.getCreatedAt();
        this.updatedAt = video.getUpdatedAt();
    }

    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) this.createdAt = LocalDateTime.now();
        if (this.updatedAt == null) this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public Video toDomain() {
        return new Video(id, userId, originalFilename, fileSizeBytes,
                mimeType, status, storageKey, createdAt, updatedAt);
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
