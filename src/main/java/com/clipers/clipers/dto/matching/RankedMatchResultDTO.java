package com.clipers.clipers.dto.matching;

import java.util.List;

public class RankedMatchResultDTO {
    private String candidateId;
    private String candidateName;
    private Double compatibilityScore;
    private Integer matchPercentage;
    private Integer rank;
    private MatchBreakdownDTO breakdown;
    private List<String> matchedSkills;
    private String explanation;

    public String getCandidateId() { return candidateId; }
    public void setCandidateId(String candidateId) { this.candidateId = candidateId; }
    public String getCandidateName() { return candidateName; }
    public void setCandidateName(String candidateName) { this.candidateName = candidateName; }
    public Double getCompatibilityScore() { return compatibilityScore; }
    public void setCompatibilityScore(Double compatibilityScore) { this.compatibilityScore = compatibilityScore; }
    public Integer getMatchPercentage() { return matchPercentage; }
    public void setMatchPercentage(Integer matchPercentage) { this.matchPercentage = matchPercentage; }
    public Integer getRank() { return rank; }
    public void setRank(Integer rank) { this.rank = rank; }
    public MatchBreakdownDTO getBreakdown() { return breakdown; }
    public void setBreakdown(MatchBreakdownDTO breakdown) { this.breakdown = breakdown; }
    public List<String> getMatchedSkills() { return matchedSkills; }
    public void setMatchedSkills(List<String> matchedSkills) { this.matchedSkills = matchedSkills; }
    public String getExplanation() { return explanation; }
    public void setExplanation(String explanation) { this.explanation = explanation; }
}