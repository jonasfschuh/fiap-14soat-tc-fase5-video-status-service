package br.com.fiap.application;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
@SpringBootApplication(scanBasePackages = "br.com.fiap")
public class VideoStatusApplication {
    public static void main(String[] args) {
        SpringApplication.run(VideoStatusApplication.class, args);
    }
}
