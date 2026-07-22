package com.airesumeanalyzer.controller;

import com.airesumeanalyzer.dto.ResumeResponse;
import com.airesumeanalyzer.service.ResumeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

// REST endpoints for the Resume Analyzer, grouped under /api/resume
@Slf4j
@RestController
@RequestMapping("/api/resume")
public class ResumeController {

    private final ResumeService resumeService;

    public ResumeController(ResumeService resumeService) {
        this.resumeService = resumeService;
    }

    // Health check
    @GetMapping("/test")
    public ResponseEntity<Map<String, String>> test() {
        return ResponseEntity.ok(Map.of(
                "status", "UP",
                "message", "AI Resume Analyzer API is up and running"
        ));
    }

    // Upload a resume PDF and get back the Groq analysis
    @PostMapping(value = "/analyze", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResumeResponse> analyzeResume(@RequestParam("file") MultipartFile file) {
        log.info("Received resume analysis request for file: {}", file.getOriginalFilename());
        ResumeResponse response = resumeService.analyzeResume(file);
        return ResponseEntity.ok(response);
    }

}
