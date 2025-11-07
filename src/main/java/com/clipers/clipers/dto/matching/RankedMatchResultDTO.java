package com.clipers.clipers.dto.matching;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * DTO de resultado individual de matching desde MicroSelectIA API
 * Coincide con RankedMatchResult de Python (Pydantic)
 * 
 * Representa un candidato rankeado con su score y desglose
 */
public class RankedMatchResultDTO {
    
    @JsonProperty("candidate_id")
    private String candidateId;
    
    @JsonProperty("candidate_name")
    private String candidateName;
    
    private Integer rank;  // 1 = mejor match
    
    @JsonProperty("compatibility_score")
    private Double compatibilityScore;  // 0.0 - 1.0
    
    @JsonProperty("match_percentage")
    private Double matchPercentage;  // 0 - 100
    
    @JsonProperty("match_quality")
    private String matchQuality;  // EXCELLENT, GOOD, FAIR, POOR
    
    private MatchBreakdownDTO breakdown;
    
    // Constructors
    public RankedMatchResultDTO() {}
    
    // Getters and Setters
    public String getCandidateId() {
        return candidateId;
    }
    
    public void setCandidateId(String candidateId) {
        this.candidateId = candidateId;
    }
    
    public String getCandidateName() {
        return candidateName;
    }
    
    public void setCandidateName(String candidateName) {
        this.candidateName = candidateName;
    }
    
    public Integer getRank() {
        return rank;
    }
    
    public void setRank(Integer rank) {
        this.rank = rank;
    }
    
    public Double getCompatibilityScore() {
        return compatibilityScore;
    }
    
    public void setCompatibilityScore(Double compatibilityScore) {
        this.compatibilityScore = compatibilityScore;
    }
    
    public Double getMatchPercentage() {
        return matchPercentage;
    }
    
    public void setMatchPercentage(Double matchPercentage) {
        this.matchPercentage = matchPercentage;
    }
    
    public String getMatchQuality() {
        return matchQuality;
    }
    
    public void setMatchQuality(String matchQuality) {
        this.matchQuality = matchQuality;
    }
    
    public MatchBreakdownDTO getBreakdown() {
        return breakdown;
    }
    
    public void setBreakdown(MatchBreakdownDTO breakdown) {
        this.breakdown = breakdown;
    }
}
