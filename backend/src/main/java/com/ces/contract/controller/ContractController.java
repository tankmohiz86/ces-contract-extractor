package com.ces.contract.controller;

import com.ces.contract.dto.ContractRecordDto;
import com.ces.contract.service.ContractService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/contracts")
@RequiredArgsConstructor
@Slf4j
public class ContractController {

    private final ContractService contractService;

    /**
     * Upload a PDF and extract CMR contract fields via Bedrock
     * POST /api/contracts/upload
     */
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadContract(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "File is empty"));
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.equalsIgnoreCase("application/pdf")) {
            return ResponseEntity.badRequest().body(Map.of("error", "Only PDF files are accepted"));
        }

        try {
            ContractRecordDto result = contractService.processUpload(file);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("Error processing upload", e);
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Extraction failed: " + e.getMessage()));
        }
    }

    /**
     * Get all extracted contracts
     * GET /api/contracts
     */
    @GetMapping
    public ResponseEntity<List<ContractRecordDto>> getAllContracts() {
        return ResponseEntity.ok(contractService.getAllContracts());
    }

    /**
     * Delete a contract record
     * DELETE /api/contracts/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteContract(@PathVariable Long id) {
        contractService.deleteContract(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Health check
     * GET /api/contracts/health
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of("status", "UP", "service", "CES Contract Extractor"));
    }
}
