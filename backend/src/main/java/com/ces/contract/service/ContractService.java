package com.ces.contract.service;

import com.ces.contract.dto.ContractRecordDto;
import com.ces.contract.entity.ContractRecord;
import com.ces.contract.repository.ContractRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ContractService {

    private final PdfExtractionService pdfExtractionService;
    private final BedrockService bedrockService;
    private final ContractRepository contractRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Full pipeline: PDF → text → Bedrock LLM → JSON → DB
     */
    public ContractRecordDto processUpload(MultipartFile file) throws Exception {
        log.info("Processing upload: {}", file.getOriginalFilename());

        // 1. Extract text from PDF
        String pdfText = pdfExtractionService.extractText(file);

        // 2. Call Bedrock to extract structured fields
        String llmJsonResponse = bedrockService.extractContractFields(pdfText);

        // 3. Parse LLM JSON into entity
        ContractRecord record = buildRecordFromLlmResponse(llmJsonResponse, file.getOriginalFilename());

        // 4. Persist to PostgreSQL
        ContractRecord saved = contractRepository.save(record);
        log.info("Saved contract record id={}, contractNumber={}", saved.getId(), saved.getContractNumber());

        return ContractRecordDto.from(saved);
    }

    public List<ContractRecordDto> getAllContracts() {
        return contractRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(ContractRecordDto::from)
                .collect(Collectors.toList());
    }

    public void deleteContract(Long id) {
        contractRepository.deleteById(id);
    }

    /**
     * Parses the LLM JSON response and maps it to a ContractRecord entity.
     * Handles cases where the LLM wraps JSON in markdown fences.
     */
    private ContractRecord buildRecordFromLlmResponse(String llmResponse, String filename) {
        ContractRecord.ContractRecordBuilder builder = ContractRecord.builder()
                .originalFilename(filename)
                .rawLlmResponse(llmResponse)
                .extractionStatus("SUCCESS");

        try {
            // Strip markdown code fences if present
            String cleaned = llmResponse.trim()
                    .replaceAll("(?s)```json\\s*", "")
                    .replaceAll("(?s)```\\s*", "")
                    .trim();

            // Find first { to last } in case there's preamble text
            int start = cleaned.indexOf('{');
            int end = cleaned.lastIndexOf('}');
            if (start >= 0 && end > start) {
                cleaned = cleaned.substring(start, end + 1);
            }

            JsonNode json = objectMapper.readTree(cleaned);

            builder
                .contractNumber(text(json, "contractNumber"))
                .workOrderNumber(text(json, "workOrderNumber"))
                .airlineOperator(text(json, "airlineOperator"))
                .aircraftType(text(json, "aircraftType"))
                .engineModel(text(json, "engineModel"))
                .engineSerialNumber(text(json, "engineSerialNumber"))
                .shopVisitDate(text(json, "shopVisitDate"))
                .customerName(text(json, "customerName"))
                .stationLocation(text(json, "stationLocation"))
                .totalCost(text(json, "totalCost"))
                .currency(text(json, "currency"))
                .workScope(text(json, "workScope"))
                .returnToServiceDate(text(json, "returnToServiceDate"))
                .authorizedSignatory(text(json, "authorizedSignatory"));

        } catch (Exception e) {
            log.warn("Failed to parse LLM JSON response: {}", e.getMessage());
            builder.extractionStatus("PARSE_ERROR");
        }

        return builder.build();
    }

    private String text(JsonNode node, String field) {
        JsonNode val = node.get(field);
        if (val == null || val.isNull()) return null;
        String text = val.asText().trim();
        return text.isEmpty() ? null : text;
    }
}
