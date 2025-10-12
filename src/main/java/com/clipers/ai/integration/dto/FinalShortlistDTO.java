package com.clipers.ai.integration.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Data Transfer Object for final candidate shortlist
 * Contains the list of candidates with their scores and ranks
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FinalShortlistDTO {
    
    @NotBlank(message = "Job ID is required")
    @Size(max = 50, message = "Job ID must be less than 50 characters")
    private String jobId;
    
    @NotEmpty(message = "Candidates list cannot be empty")
    @Size(min = 1, max = 1000, message = "Candidates list must contain between 1 and 1000 candidates")
    @Valid
    private List<CandidateScoreDTO> candidates;
    
    private boolean cached;
    
    @NotNull(message = "Generated at timestamp is required")
    private LocalDateTime generatedAt;
    
    // Builder pattern for flexible object creation
    public static class FinalShortlistDTOBuilder {
        // Custom builder methods can be added here if needed
    }
}