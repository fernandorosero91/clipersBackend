package com.clipers.ai.integration.dto;

import jakarta.validation.constraints.*;
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
    
    @NotBlank(message = "Candidate ID is required")
    @Size(max = 50, message = "Candidate ID must be less than 50 characters")
    private String candidateId;
    
    @NotBlank(message = "Candidate name is required")
    @Size(max = 100, message = "Candidate name must be less than 100 characters")
    private String candidateName;
    
    @DecimalMin(value = "0.0", message = "AI score must be at least 0.0")
    @DecimalMax(value = "1.0", message = "AI score must be at most 1.0")
    private Double aiScore;
    
    @DecimalMin(value = "0.0", message = "ATS score must be at least 0.0")
    @DecimalMax(value = "1.0", message = "ATS score must be at most 1.0")
    private Double atsScore;
    
    @DecimalMin(value = "0.0", message = "Profile score must be at least 0.0")
    @DecimalMax(value = "1.0", message = "Profile score must be at most 1.0")
    private Double profileScore;
    
    @DecimalMin(value = "0.0", message = "Final score must be at least 0.0")
    @DecimalMax(value = "1.0", message = "Final score must be at most 1.0")
    private Double finalScore;
    
    @Min(value = 1, message = "Rank must be at least 1")
    private int rank;
    
    @Pattern(regexp = "^(PRESELECTED|SELECTED|REVIEW)$", message = "State must be one of: PRESELECTED, SELECTED, REVIEW")
    private String state;
    
    // Builder pattern for flexible object creation
    public static class CandidateScoreDTOBuilder {
        // Custom builder methods can be added here if needed
    }
}