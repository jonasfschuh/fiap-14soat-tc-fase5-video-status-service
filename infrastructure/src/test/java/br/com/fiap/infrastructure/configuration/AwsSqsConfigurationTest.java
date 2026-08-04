package br.com.fiap.infrastructure.configuration;

import br.com.fiap.domain.ports.in.RegisterVideoUploadedInputPort;
import br.com.fiap.domain.ports.in.UpdateVideoStatusInputPort;
import br.com.fiap.infrastructure.adapters.messaging.VideoEventsSqsConsumer;
import br.com.fiap.infrastructure.adapters.messaging.VideoUploadedSqsConsumer;
import br.com.fiap.infrastructure.logging.SqsMessageLogger;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import software.amazon.awssdk.services.sqs.SqsClient;

import java.lang.reflect.Field;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("AwsSqsConfiguration - Unit Tests")
class AwsSqsConfigurationTest {

    private AwsSqsConfiguration config;

    @BeforeEach
    void setUp() throws Exception {
        config = new AwsSqsConfiguration();
        setField("region", "us-east-1");
        setField("accessKeyId", "test");
        setField("secretAccessKey", "test");
        setField("videoUploadedQueueUrl", "http://localhost:4566/000000000000/video-uploaded");
        setField("videoEventsQueueUrl", "http://localhost:4566/000000000000/video-events");
    }

    @Test
    @DisplayName("sqsClient with endpoint override builds using static credentials")
    void sqsClient_withEndpointOverride_buildsSuccessfully() throws Exception {
        setField("endpointOverride", "http://localhost:4566");

        SqsClient client = config.sqsClient();
        assertThat(client).isNotNull();
        client.close();
    }

    @Test
    @DisplayName("sqsClient without endpoint override builds using default credentials")
    void sqsClient_withoutEndpointOverride_buildsSuccessfully() throws Exception {
        setField("endpointOverride", null);

        SqsClient client = config.sqsClient();
        assertThat(client).isNotNull();
        client.close();
    }

    @Test
    @DisplayName("videoUploadedSqsConsumer returns a configured consumer instance")
    void videoUploadedSqsConsumer_returnsConsumer() {
        SqsClient sqsClient = Mockito.mock(SqsClient.class);
        ObjectMapper mapper = new ObjectMapper();
        SqsMessageLogger logger = new SqsMessageLogger(mapper);
        RegisterVideoUploadedInputPort port = Mockito.mock(RegisterVideoUploadedInputPort.class);

        VideoUploadedSqsConsumer consumer = config.videoUploadedSqsConsumer(sqsClient, mapper, logger, port);

        assertThat(consumer).isNotNull();
    }

    @Test
    @DisplayName("videoEventsSqsConsumer returns a configured consumer instance")
    void videoEventsSqsConsumer_returnsConsumer() {
        SqsClient sqsClient = Mockito.mock(SqsClient.class);
        ObjectMapper mapper = new ObjectMapper();
        SqsMessageLogger logger = new SqsMessageLogger(mapper);
        UpdateVideoStatusInputPort port = Mockito.mock(UpdateVideoStatusInputPort.class);

        VideoEventsSqsConsumer consumer = config.videoEventsSqsConsumer(sqsClient, mapper, logger, port);

        assertThat(consumer).isNotNull();
    }

    private void setField(String name, Object value) throws Exception {
        Field f = AwsSqsConfiguration.class.getDeclaredField(name);
        f.setAccessible(true);
        f.set(config, value);
    }
}
