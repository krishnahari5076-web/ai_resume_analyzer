package com.airesumeanalyzer.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

// Response sent back to the frontend after analysis
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResumeResponse {

    private String fileName;
    private Integer atsScore;
    private String summary;
    private List<String> strengths;
    private List<String> weaknesses;
    private List<String> missingSkills;
    private List<String> suggestions;
    private List<String> keywords;
}
