package com.clipers.clipers.dto.matching;

public class ExplainMatchRequestDTO {
    private CandidateDTO candidate;
    private JobDTO job;
    private Boolean includeSuggestions = Boolean.TRUE;

    public CandidateDTO getCandidate() { return candidate; }
    public void setCandidate(CandidateDTO candidate) { this.candidate = candidate; }
    public JobDTO getJob() { return job; }
    public void setJob(JobDTO job) { this.job = job; }
    public Boolean getIncludeSuggestions() { return includeSuggestions; }
    public void setIncludeSuggestions(Boolean includeSuggestions) { this.includeSuggestions = includeSuggestions; }
}