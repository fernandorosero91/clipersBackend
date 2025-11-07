package com.clipers.clipers.dto.matching;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/**
 * DTO de response del endpoint /api/match/batch de MicroSelectIA
 * Coincide con BatchMatchResponse de Python (Pydantic)
 * 
 * Contiene la lista de candidatos rankeados ordenados por score
 */
public class BatchMatchResponseDTO {
    
    @JsonProperty("job_id")
    private String jobId;
    
    @JsonProperty("ranked_candidates")
    private List<RankedMatchResultDTO> rankedCandidates;
    
    @JsonProperty("total_candidates")
    private Integer totalCandidates;
    
    // Constructors
    public BatchMatchResponseDTO() {}
    
    // Getters and Setters
    public String getJobId() {
        return jobId;
    }
    
    public void setJobId(String jobId) {
        this.jobId = jobId;
    }
    
    public List<RankedMatchResultDTO> getRankedCandidates() {
        return rankedCandidates;
    }
    
    public void setRankedCandidates(List<RankedMatchResultDTO> rankedCandidates) {
        this.rankedCandidates = rankedCandidates;
    }
    
    public Integer getTotalCandidates() {
        return totalCandidates;
    }
    
    public void setTotalCandidates(Integer totalCandidates) {
        this.totalCandidates = totalCandidates;
    }
}
