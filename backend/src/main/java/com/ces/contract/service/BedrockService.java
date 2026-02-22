package com.ces.contract.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.bedrockruntime.BedrockRuntimeClient;
import software.amazon.awssdk.services.bedrockruntime.model.InvokeModelRequest;
import software.amazon.awssdk.services.bedrockruntime.model.InvokeModelResponse;

@Service
@Slf4j
@RequiredArgsConstructor
public class BedrockService {

    private final BedrockRuntimeClient bedrockRuntimeClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${aws.bedrock.model-id:amazon.titan-text-lite-v1}")
    private String modelId;

    private static final String PROMPT_TEMPLATE = """
            You are an aviation contract data extraction assistant for GE Aerospace CES.
            Extract the following fields from the CMR (Component Maintenance Report) contract text below.
            Return ONLY a valid JSON object with exactly these keys. Use null for any field not found.

            {
              "contractNumber": "",
              "workOrderNumber": "",
              "airlineOperator": "",
              "aircraftType": "",
              "engineModel": "",
              "engineSerialNumber": "",
              "shopVisitDate": "",
              "customerName": "",
              "stationLocation": "",
              "totalCost": "",
              "currency": "",
              "workScope": "",
              "returnToServiceDate": "",
              "authorizedSignatory": ""
            }

            CONTRACT TEXT:
            %s

            Return ONLY the JSON object, no explanation, no markdown fences.
            """;

    /**
     * Invokes AWS Bedrock Titan Text Lite v1 to extract structured contract fields from PDF text.
     * AWS SDK v2.41.31
     */
    public String extractContractFields(String pdfText) {
        // Truncate to ~3000 chars to stay within token limits for Titan Lite
        String truncated = pdfText.length() > 3000 ? pdfText.substring(0, 3000) + "..." : pdfText;

        String prompt = PROMPT_TEMPLATE.formatted(truncated);

        // Build the Titan Text request body
        String requestBody = buildTitanRequestBody(prompt);

        log.debug("Invoking Bedrock model: {}", modelId);

        InvokeModelRequest request = InvokeModelRequest.builder()
                .modelId(modelId)
                .contentType("application/json")
                .accept("application/json")
                .body(SdkBytes.fromUtf8String(requestBody))
                .build();

        InvokeModelResponse response = bedrockRuntimeClient.invokeModel(request);
        String rawResponse = response.body().asUtf8String();

        log.debug("Bedrock raw response: {}", rawResponse);

        return parseBedrockResponse(rawResponse);
    }

    /**
     * Builds the JSON request body for amazon.titan-text-lite-v1
     */
    private String buildTitanRequestBody(String prompt) {
        try {
            // Escape prompt for JSON embedding
            String escapedPrompt = objectMapper.writeValueAsString(prompt);
            // Remove outer quotes added by writeValueAsString
            escapedPrompt = escapedPrompt.substring(1, escapedPrompt.length() - 1);

            return """
                    {
                      "inputText": "%s",
                      "textGenerationConfig": {
                        "maxTokenCount": 1000,
                        "temperature": 0.1,
                        "topP": 0.9,
                        "stopSequences": []
                      }
                    }
                    """.formatted(escapedPrompt);
        } catch (Exception e) {
            throw new RuntimeException("Failed to build Bedrock request body", e);
        }
    }

    /**
     * Extracts the generated text from the Titan response envelope
     * Response format: {"inputTextTokenCount":N,"results":[{"tokenCount":N,"outputText":"...","completionReason":"FINISH"}]}
     */
    private String parseBedrockResponse(String rawResponse) {
        try {
            JsonNode root = objectMapper.readTree(rawResponse);
            JsonNode results = root.path("results");
            if (results.isArray() && results.size() > 0) {
                return results.get(0).path("outputText").asText();
            }
            return rawResponse;
        } catch (Exception e) {
            log.warn("Could not parse Bedrock response envelope, returning raw: {}", e.getMessage());
            return rawResponse;
        }
    }
}
