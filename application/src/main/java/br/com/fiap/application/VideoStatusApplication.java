package br.com.fiap.application;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = "br.com.fiap")
@EnableScheduling
public class VideoStatusApplication {
    public static void main(String[] args) {
        SpringApplication.run(VideoStatusApplication.class, args);
    }
}
