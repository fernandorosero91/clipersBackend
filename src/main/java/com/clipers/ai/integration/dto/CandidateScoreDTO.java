package com.clipers.ai.integration.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Data Transfer Object for candidate scores
 * Contains individual scores and final calculated score
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CandidateScoreDTO {
    
    private String candidateId;
    private String candidateName;
    private Double aiScore;
    private Double atsScore;
    private Double profileScore;
    private Double finalScore;
    private int rank;
    private String state;
    
    // Builder pattern for flexible object creation
    public static class CandidateScoreDTOBuilder {
        // Custom builder methods can be added here if needed
    }
}