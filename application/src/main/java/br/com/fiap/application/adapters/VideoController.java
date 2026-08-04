package br.com.fiap.application.adapters;

import br.com.fiap.application.dtos.VideoStatusResponse;
import br.com.fiap.application.mappers.VideoMapper;
import br.com.fiap.domain.model.Video;
import br.com.fiap.domain.ports.in.FindVideoByIdInputPort;
import br.com.fiap.domain.ports.in.FindVideosByUserInputPort;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/videos")
@Tag(name = "Videos", description = "Video status query")
public class VideoController {

    private final FindVideosByUserInputPort findVideosByUser;
    private final FindVideoByIdInputPort findVideoById;

    public VideoController(FindVideosByUserInputPort findVideosByUser,
                            FindVideoByIdInputPort findVideoById) {
        this.findVideosByUser = findVideosByUser;
        this.findVideoById = findVideoById;
    }

    @GetMapping
    @Operation(summary = "List user videos", description = "Returns the status of all videos uploaded by the authenticated user.")
    public ResponseEntity<List<VideoStatusResponse>> listByUser(
            @RequestHeader("X-User-Id") String userId) {

        List<VideoStatusResponse> videos = findVideosByUser.execute(userId)
                .stream()
                .map(VideoMapper::toStatusResponse)
                .toList();

        return ResponseEntity.ok(videos);
    }

    @GetMapping("/{videoId}")
    @Operation(summary = "Get video status by ID", description = "Returns status and metadata for a specific video.")
    public ResponseEntity<VideoStatusResponse> getById(
            @RequestHeader("X-User-Id") String userId,
            @PathVariable UUID videoId) {

        Video video = findVideoById.execute(videoId, userId);
        return ResponseEntity.ok(VideoMapper.toStatusResponse(video));
    }
}
