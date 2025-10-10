package com.clipers.ai.integration.dto;

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
    
    private String jobId;
    private List<CandidateScoreDTO> candidates;
    private boolean cached;
    private LocalDateTime generatedAt;
    
    // Builder pattern for flexible object creation
    public static class FinalShortlistDTOBuilder {
        // Custom builder methods can be added here if needed
    }
}