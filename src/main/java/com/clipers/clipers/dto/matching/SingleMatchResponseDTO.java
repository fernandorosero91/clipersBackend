package com.clipers.clipers.dto.matching;

import java.util.List;

public class SingleMatchResponseDTO {
    private String candidateId;
    private String candidateName;
    private String jobId;
    private Double compatibilityScore;
    private Integer matchPercentage;
    private MatchBreakdownDTO breakdown;
    private List<String> matchedSkills;
    private List<String> missingSkills;
    private String explanation;
    private List<String> recommendations;
    private String matchQuality;

    public String getCandidateId() { return candidateId; }
    public void setCandidateId(String candidateId) { this.candidateId = candidateId; }
    public String getCandidateName() { return candidateName; }
    public void setCandidateName(String candidateName) { this.candidateName = candidateName; }
    public String getJobId() { return jobId; }
    public void setJobId(String jobId) { this.jobId = jobId; }
    public Double getCompatibilityScore() { return compatibilityScore; }
    public void setCompatibilityScore(Double compatibilityScore) { this.compatibilityScore = compatibilityScore; }
    public Integer getMatchPercentage() { return matchPercentage; }
    public void setMatchPercentage(Integer matchPercentage) { this.matchPercentage = matchPercentage; }
    public MatchBreakdownDTO getBreakdown() { return breakdown; }
    public void setBreakdown(MatchBreakdownDTO breakdown) { this.breakdown = breakdown; }
    public List<String> getMatchedSkills() { return matchedSkills; }
    public void setMatchedSkills(List<String> matchedSkills) { this.matchedSkills = matchedSkills; }
    public List<String> getMissingSkills() { return missingSkills; }
    public void setMissingSkills(List<String> missingSkills) { this.missingSkills = missingSkills; }
    public String getExplanation() { return explanation; }
    public void setExplanation(String explanation) { this.explanation = explanation; }
    public List<String> getRecommendations() { return recommendations; }
    public void setRecommendations(List<String> recommendations) { this.recommendations = recommendations; }
    public String getMatchQuality() { return matchQuality; }
    public void setMatchQuality(String matchQuality) { this.matchQuality = matchQuality; }
}