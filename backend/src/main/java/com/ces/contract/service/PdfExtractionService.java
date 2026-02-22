package com.ces.contract.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@Slf4j
public class PdfExtractionService {

    /**
     * Extracts plain text from an uploaded PDF file using Apache PDFBox 3.x
     */
    public String extractText(MultipartFile file) throws IOException {
        log.debug("Extracting text from PDF: {}", file.getOriginalFilename());

        try (PDDocument document = PDDocument.load(file.getInputStream())) {
            if (document.isEncrypted()) {
                throw new IllegalArgumentException("Encrypted PDFs are not supported.");
            }

            PDFTextStripper stripper = new PDFTextStripper();
            stripper.setSortByPosition(true);
            String text = stripper.getText(document);

            log.debug("Extracted {} characters from PDF", text.length());
            return text;
        }
    }
}
