package com.airesumeanalyzer.util;

import com.airesumeanalyzer.exception.FileProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

// Extracts text from a PDF resume using PDFBox
@Slf4j
@Component
public class PdfExtractor {

    public String extractText(MultipartFile file) {
        try (PDDocument document = PDDocument.load(file.getInputStream())) {
            if (document.isEncrypted()) {
                throw new FileProcessingException("The uploaded PDF is password protected and cannot be read.");
            }

            PDFTextStripper stripper = new PDFTextStripper();
            stripper.setSortByPosition(true);

            String text = stripper.getText(document);

            if (text == null || text.isBlank()) {
                throw new FileProcessingException(
                        "No readable text could be extracted from the PDF. It may be a scanned image.");
            }

            log.debug("Extracted {} characters from PDF '{}'", text.length(), file.getOriginalFilename());
            return text.trim();

        } catch (IOException e) {
            log.error("Failed to extract text from PDF '{}'", file.getOriginalFilename(), e);
            throw new FileProcessingException("Unable to read the uploaded PDF file.", e);
        }
    }
}
