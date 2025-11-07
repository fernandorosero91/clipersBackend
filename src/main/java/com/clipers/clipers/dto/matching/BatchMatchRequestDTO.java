package com.clipers.clipers.dto.matching;

import java.util.List;

public class BatchMatchRequestDTO {
    private List<CandidateDTO> candidates;
    private JobDTO job;

    public List<CandidateDTO> getCandidates() { return candidates; }
    public void setCandidates(List<CandidateDTO> candidates) { this.candidates = candidates; }
    public JobDTO getJob() { return job; }
    public void setJob(JobDTO job) { this.job = job; }
}