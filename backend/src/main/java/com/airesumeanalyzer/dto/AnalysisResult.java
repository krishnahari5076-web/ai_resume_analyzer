package com.airesumeanalyzer.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

// JSON structure returned by the AI provider.
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class AnalysisResult {

    private Integer atsScore;
    private String summary;
    private List<String> strengths;
    private List<String> weaknesses;
    private List<String> missingSkills;
    private List<String> suggestions;
    private List<String> keywords;
}
