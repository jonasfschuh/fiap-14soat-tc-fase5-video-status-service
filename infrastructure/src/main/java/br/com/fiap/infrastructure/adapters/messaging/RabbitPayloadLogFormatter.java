package br.com.fiap.infrastructure.adapters.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;

final class RabbitPayloadLogFormatter {

    private RabbitPayloadLogFormatter() {
    }

    static String prettyJson(ObjectMapper objectMapper, String payload) {
        try {
            Object jsonBody = objectMapper.readValue(payload, Object.class);
            // If the payload was double-encoded (a JSON string containing JSON), unwrap it first
            if (jsonBody instanceof String inner) {
                jsonBody = objectMapper.readValue(inner, Object.class);
            }
            return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonBody);
        } catch (Exception ignored) {
            return payload;
        }
    }
}
