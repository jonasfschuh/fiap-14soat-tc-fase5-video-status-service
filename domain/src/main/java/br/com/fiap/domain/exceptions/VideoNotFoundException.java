package br.com.fiap.domain.exceptions;

public class VideoNotFoundException extends RuntimeException {
    public VideoNotFoundException(String videoId) {
        super("Video not found: " + videoId);
    }
}
