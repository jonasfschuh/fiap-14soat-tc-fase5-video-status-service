package br.com.fiap.infrastructure.configuration;

import br.com.fiap.domain.ports.in.RegisterVideoUploadedInputPort;
import br.com.fiap.domain.ports.in.UpdateVideoStatusInputPort;
import br.com.fiap.infrastructure.adapters.messaging.VideoEventsSqsConsumer;
import br.com.fiap.infrastructure.adapters.messaging.VideoUploadedSqsConsumer;
import br.com.fiap.infrastructure.logging.SqsMessageLogger;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.SqsClientBuilder;

import java.net.URI;

@Configuration
public class AwsSqsConfiguration {

    @Value("${aws.region:us-east-1}")
    private String region;

    @Value("${aws.access-key-id:test}")
    private String accessKeyId;

    @Value("${aws.secret-access-key:test}")
    private String secretAccessKey;

    @Value("${aws.endpoint-override:#{null}}")
    private String endpointOverride;

    @Value("${aws.sqs.queues.video-uploaded:http://localhost:4566/000000000000/video-uploaded}")
    private String videoUploadedQueueUrl;

    @Value("${aws.sqs.queues.video-events:http://localhost:4566/000000000000/video-events}")
    private String videoEventsQueueUrl;

    @Bean
    @ConditionalOnProperty(name = "aws.sqs.enabled", havingValue = "true", matchIfMissing = false)
    public SqsClient sqsClient() {
        var builder = SqsClient.builder().region(Region.of(region));
        applyEndpoint(builder, endpointOverride);
        return builder.build();
    }

    @Bean
    @ConditionalOnProperty(name = "aws.sqs.enabled", havingValue = "true", matchIfMissing = false)
    public VideoUploadedSqsConsumer videoUploadedSqsConsumer(SqsClient sqsClient,
                                                              ObjectMapper objectMapper,
                                                              SqsMessageLogger sqsMessageLogger,
                                                              RegisterVideoUploadedInputPort registerVideoUploaded) {
        return new VideoUploadedSqsConsumer(sqsClient, objectMapper, sqsMessageLogger, registerVideoUploaded, videoUploadedQueueUrl);
    }

    @Bean
    @ConditionalOnProperty(name = "aws.sqs.enabled", havingValue = "true", matchIfMissing = false)
    public VideoEventsSqsConsumer videoEventsSqsConsumer(SqsClient sqsClient,
                                                          ObjectMapper objectMapper,
                                                          SqsMessageLogger sqsMessageLogger,
                                                          UpdateVideoStatusInputPort updateVideoStatus) {
        return new VideoEventsSqsConsumer(sqsClient, objectMapper, sqsMessageLogger, updateVideoStatus, videoEventsQueueUrl);
    }

    private void applyEndpoint(SqsClientBuilder builder, String endpoint) {
        if (endpoint != null && !endpoint.isBlank()) {
            builder.endpointOverride(URI.create(endpoint))
                   .credentialsProvider(StaticCredentialsProvider.create(
                           AwsBasicCredentials.create(accessKeyId, secretAccessKey)));
        } else {
            builder.credentialsProvider(DefaultCredentialsProvider.create());
        }
    }
}
