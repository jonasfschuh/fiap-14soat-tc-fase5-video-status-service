package br.com.fiap.application.bdd.steps;

import br.com.fiap.domain.model.VideoStatus;
import br.com.fiap.domain.ports.in.RegisterVideoUploadedInputPort;
import br.com.fiap.domain.ports.in.UpdateVideoStatusInputPort;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

public class VideoStatusSteps {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private RegisterVideoUploadedInputPort registerVideoUploaded;

    @Autowired
    private UpdateVideoStatusInputPort updateVideoStatus;

    private UUID currentVideoId;
    private String currentUserId;
    private MvcResult lastResult;

    @Given("a video {string} was uploaded by user {string} with status {string}")
    public void aVideoWasUploadedByUserWithStatus(String filename, String userId, String status) {
        currentVideoId = UUID.randomUUID();
        registerVideoUploaded.execute(currentVideoId, userId, filename, 1024L, "video/mp4", "videos/" + userId + "/" + filename);
        updateVideoStatus.execute(currentVideoId, VideoStatus.valueOf(status));
    }

    @Given("the user {string} has previously uploaded videos")
    public void theUserHasPreviouslyUploadedVideos(String userId) {
        this.currentUserId = userId;
        registerVideoUploaded.execute(UUID.randomUUID(), userId, "test.mp4", 1024L, "video/mp4", "videos/" + userId + "/test.mp4");
    }

    @When("the user {string} requests the video status")
    public void theUserRequestsTheVideoStatus(String userId) throws Exception {
        lastResult = mockMvc.perform(get("/api/videos/" + currentVideoId).header("X-User-Id", userId)).andReturn();
    }

    @When("the user {string} requests a random non-existing video")
    public void theUserRequestsARandomNonExistingVideo(String userId) throws Exception {
        lastResult = mockMvc.perform(get("/api/videos/" + UUID.randomUUID()).header("X-User-Id", userId)).andReturn();
    }

    @When("the user requests their video list")
    public void theUserRequestsTheirVideoList() throws Exception {
        lastResult = mockMvc.perform(get("/api/videos").header("X-User-Id", currentUserId)).andReturn();
    }

    @Then("the response status should be {int}")
    public void theResponseStatusShouldBe(int status) {
        assertThat(lastResult.getResponse().getStatus()).isEqualTo(status);
    }

    @Then("the video status should be {string}")
    public void theVideoStatusShouldBe(String status) throws Exception {
        String body = lastResult.getResponse().getContentAsString();
        assertThat(body).contains(status);
    }

    @Then("the response should contain a list of videos")
    public void theResponseShouldContainAListOfVideos() throws Exception {
        String body = lastResult.getResponse().getContentAsString();
        assertThat(body).startsWith("[");
    }
}
