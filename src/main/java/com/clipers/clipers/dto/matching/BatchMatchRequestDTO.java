package com.clipers.clipers.dto.matching;

import java.util.List;

/**
 * DTO de request para el endpoint /api/match/batch de MicroSelectIA
 * Coincide con BatchMatchRequest de Python (Pydantic)
 * 
 * Envía múltiples candidatos y un job para obtener ranking
 */
public class BatchMatchRequestDTO {
    
    private List<CandidateDTO> candidates;
    private JobForMatchingDTO job;
    
    // Constructors
    public BatchMatchRequestDTO() {}
    
    public BatchMatchRequestDTO(List<CandidateDTO> candidates, JobForMatchingDTO job) {
        this.candidates = candidates;
        this.job = job;
    }
    
    // Getters and Setters
    public List<CandidateDTO> getCandidates() {
        return candidates;
    }
    
    public void setCandidates(List<CandidateDTO> candidates) {
        this.candidates = candidates;
    }
    
    public JobForMatchingDTO getJob() {
        return job;
    }
    
    public void setJob(JobForMatchingDTO job) {
        this.job = job;
    }
}
