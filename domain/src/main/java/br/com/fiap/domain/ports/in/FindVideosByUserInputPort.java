package br.com.fiap.domain.ports.in;

import br.com.fiap.domain.model.Video;

import java.util.List;

public interface FindVideosByUserInputPort {
    List<Video> execute(String userId);
}
