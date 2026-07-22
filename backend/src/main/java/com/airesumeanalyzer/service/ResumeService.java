package com.airesumeanalyzer.service;

import com.airesumeanalyzer.dto.AnalysisResult;
import com.airesumeanalyzer.dto.ResumeResponse;
import com.airesumeanalyzer.exception.InvalidFileException;
import com.airesumeanalyzer.util.PdfExtractor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

// Main service: validates the upload, extracts text, calls Groq, and returns the result.
@Slf4j
@Service
public class ResumeService {

    private static final long MAX_FILE_SIZE_BYTES = 10L * 1024 * 1024; // 10MB
    private static final String PDF_CONTENT_TYPE = "application/pdf";

    private final PdfExtractor pdfExtractor;
    private final GroqService groqService;

    public ResumeService(PdfExtractor pdfExtractor, GroqService groqService) {
        this.pdfExtractor = pdfExtractor;
        this.groqService = groqService;
    }

    // Full pipeline: validate -> extract text -> call Groq -> return JSON
    public ResumeResponse analyzeResume(MultipartFile file) {
        validateFile(file);

        log.info("Starting analysis for file '{}' ({} bytes)", file.getOriginalFilename(), file.getSize());

        String resumeText = pdfExtractor.extractText(file);
        AnalysisResult analysis = groqService.analyzeResume(resumeText);

        return ResumeResponse.builder()
                .fileName(file.getOriginalFilename())
                .atsScore(analysis.getAtsScore())
                .summary(analysis.getSummary())
                .strengths(analysis.getStrengths())
                .weaknesses(analysis.getWeaknesses())
                .missingSkills(analysis.getMissingSkills())
                .suggestions(analysis.getSuggestions())
                .keywords(analysis.getKeywords())
                .build();
    }

    // Only PDFs under 10MB are accepted
    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new InvalidFileException("Please upload a resume file.");
        }

        if (file.getSize() > MAX_FILE_SIZE_BYTES) {
            throw new InvalidFileException("File size exceeds the maximum allowed limit of 10MB.");
        }

        String contentType = file.getContentType();
        String originalFilename = file.getOriginalFilename();
        boolean hasPdfExtension = originalFilename != null && originalFilename.toLowerCase().endsWith(".pdf");

        if (!PDF_CONTENT_TYPE.equals(contentType) || !hasPdfExtension) {
            throw new InvalidFileException("Only PDF files are supported. Please upload a .pdf resume.");
        }
    }
}
