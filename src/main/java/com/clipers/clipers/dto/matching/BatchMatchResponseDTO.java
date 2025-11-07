package com.clipers.clipers.dto.matching;

import java.util.List;

public class BatchMatchResponseDTO {
    private String jobId;
    private String jobTitle;
    private Integer totalCandidates;
    private List<RankedMatchResultDTO> matches;
    private Double averageScore;
    private List<String> topSkillsMatched;

    public String getJobId() { return jobId; }
    public void setJobId(String jobId) { this.jobId = jobId; }
    public String getJobTitle() { return jobTitle; }
    public void setJobTitle(String jobTitle) { this.jobTitle = jobTitle; }
    public Integer getTotalCandidates() { return totalCandidates; }
    public void setTotalCandidates(Integer totalCandidates) { this.totalCandidates = totalCandidates; }
    public List<RankedMatchResultDTO> getMatches() { return matches; }
    public void setMatches(List<RankedMatchResultDTO> matches) { this.matches = matches; }
    public Double getAverageScore() { return averageScore; }
    public void setAverageScore(Double averageScore) { this.averageScore = averageScore; }
    public List<String> getTopSkillsMatched() { return topSkillsMatched; }
    public void setTopSkillsMatched(List<String> topSkillsMatched) { this.topSkillsMatched = topSkillsMatched; }
}