package com.clipers.clipers.dto.matching;

import java.util.List;
import java.util.Map;

public class ExplainMatchResponseDTO {
    private String candidateId;
    private String jobId;
    private Double compatibilityScore;
    private Integer matchPercentage;
    private MatchBreakdownDTO breakdown;
    private Map<String, String> detailedAnalysis;
    private List<String> strengths;
    private List<String> weaknesses;
    private List<String> suggestions;
    private String decisionRecommendation;

    public String getCandidateId() { return candidateId; }
    public void setCandidateId(String candidateId) { this.candidateId = candidateId; }
    public String getJobId() { return jobId; }
    public void setJobId(String jobId) { this.jobId = jobId; }
    public Double getCompatibilityScore() { return compatibilityScore; }
    public void setCompatibilityScore(Double compatibilityScore) { this.compatibilityScore = compatibilityScore; }
    public Integer getMatchPercentage() { return matchPercentage; }
    public void setMatchPercentage(Integer matchPercentage) { this.matchPercentage = matchPercentage; }
    public MatchBreakdownDTO getBreakdown() { return breakdown; }
    public void setBreakdown(MatchBreakdownDTO breakdown) { this.breakdown = breakdown; }
    public Map<String, String> getDetailedAnalysis() { return detailedAnalysis; }
    public void setDetailedAnalysis(Map<String, String> detailedAnalysis) { this.detailedAnalysis = detailedAnalysis; }
    public List<String> getStrengths() { return strengths; }
    public void setStrengths(List<String> strengths) { this.strengths = strengths; }
    public List<String> getWeaknesses() { return weaknesses; }
    public void setWeaknesses(List<String> weaknesses) { this.weaknesses = weaknesses; }
    public List<String> getSuggestions() { return suggestions; }
    public void setSuggestions(List<String> suggestions) { this.suggestions = suggestions; }
    public String getDecisionRecommendation() { return decisionRecommendation; }
    public void setDecisionRecommendation(String decisionRecommendation) { this.decisionRecommendation = decisionRecommendation; }
}