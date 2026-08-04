package br.com.fiap.domain.exceptions;

public class VideoAccessDeniedException extends RuntimeException {
    public VideoAccessDeniedException(String videoId) {
        super("Access denied to video: " + videoId);
    }
}
