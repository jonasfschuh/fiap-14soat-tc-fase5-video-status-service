package br.com.fiap.application.unit.controllers;

import br.com.fiap.application.adapters.VideoController;
import br.com.fiap.domain.model.Video;
import br.com.fiap.domain.model.VideoStatus;
import br.com.fiap.domain.ports.in.FindVideoByIdInputPort;
import br.com.fiap.domain.ports.in.FindVideosByUserInputPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VideoControllerTest {

    @Mock private FindVideosByUserInputPort findVideosByUser;
    @Mock private FindVideoByIdInputPort findVideoById;

    @Test
    void shouldReturn200OnListByUser() {
        VideoController controller = new VideoController(findVideosByUser, findVideoById);
        Video video = Video.createWithId(UUID.randomUUID(), "user-1", "test.mp4", 1024L, "video/mp4", "videos/user-1/test.mp4");
        when(findVideosByUser.execute("user-1")).thenReturn(List.of(video));

        ResponseEntity<?> response = controller.listByUser("user-1");

        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody()).asList().hasSize(1);
    }

    @Test
    void shouldReturn200OnListAllWhenNoUserIdProvided() {
        VideoController controller = new VideoController(findVideosByUser, findVideoById);
        Video v1 = Video.createWithId(UUID.randomUUID(), "user-1", "a.mp4", 1024L, "video/mp4", "key1");
        Video v2 = Video.createWithId(UUID.randomUUID(), "user-2", "b.mp4", 512L, "video/mp4", "key2");
        when(findVideosByUser.execute(null)).thenReturn(List.of(v1, v2));

        ResponseEntity<?> response = controller.listByUser(null);

        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody()).asList().hasSize(2);
    }

    @Test
    void shouldReturn200OnGetById() {
        VideoController controller = new VideoController(findVideosByUser, findVideoById);
        UUID id = UUID.randomUUID();
        Video video = Video.createWithId(id, "user-1", "test.mp4", 1024L, "video/mp4", "videos/user-1/test.mp4");
        video.setStatus(VideoStatus.DONE);
        when(findVideoById.execute(id, "user-1")).thenReturn(video);

        ResponseEntity<?> response = controller.getById("user-1", id);

        assertThat(response.getStatusCode().value()).isEqualTo(200);
    }
}
