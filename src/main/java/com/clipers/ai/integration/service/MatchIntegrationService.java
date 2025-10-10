package com.clipers.ai.integration.service;

import com.clipers.ai.integration.dto.CandidateScoreDTO;
import com.clipers.ai.integration.dto.FinalShortlistDTO;
import com.clipers.clipers.entity.Job;
import com.clipers.clipers.entity.JobMatch;
import com.clipers.clipers.entity.User;
import com.clipers.clipers.repository.JobMatchRepository;
import com.clipers.clipers.repository.JobRepository;
import com.clipers.clipers.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service for AI + ATS integration
 * Orchestrates candidate matching, scoring, and caching
 */
@Service
public class MatchIntegrationService {

    private static final Logger logger = LoggerFactory.getLogger(MatchIntegrationService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private JobMatchRepository jobMatchRepository;

    @Autowired
    private ATSProfileEvaluator atsProfileEvaluator;

    @Autowired
    private ScoreAggregator scoreAggregator;

    @Autowired
    private ShortlistCacheService shortlistCacheService;

    @Value("${ai.score.weights.ai:0.6}")
    private double aiScoreWeight;

    @Value("${ai.score.weights.ats:0.3}")
    private double atsScoreWeight;

    @Value("${ai.score.weights.profile:0.1}")
    private double profileScoreWeight;

    /**
     * Generates a new shortlist for a job
     * @param jobId The job ID
     * @return FinalShortlistDTO with the generated shortlist
     */
    public FinalShortlistDTO generateShortlist(String jobId) {
        logger.info("Generating shortlist for job: {}", jobId);
        
        // Get job from repository
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Job not found: " + jobId));
        
        // Get all candidates
        List<User> candidates = userRepository.findAll();
        logger.debug("Found {} candidates for job: {}", candidates.size(), jobId);
        
        // Evaluate each candidate
        List<CandidateScoreDTO> candidateScores = candidates.stream()
                .map(candidate -> evaluateCandidate(candidate, job))
                .filter(Objects::nonNull)
                .sorted((c1, c2) -> Double.compare(c2.getFinalScore(), c1.getFinalScore()))
                .collect(Collectors.toList());
        
        // Assign ranks and states
        assignRanksAndStates(candidateScores);
        
        // Create final shortlist
        FinalShortlistDTO shortlist = FinalShortlistDTO.builder()
                .jobId(jobId)
                .candidates(candidateScores)
                .cached(false)
                .generatedAt(LocalDateTime.now())
                .build();
        
        // Cache the shortlist
        shortlistCacheService.cacheShortlist(jobId, shortlist);
        
        logger.info("Generated shortlist with {} candidates for job: {}", candidateScores.size(), jobId);
        return shortlist;
    }

    /**
     * Retrieves a shortlist from cache or generates a new one if not cached
     * @param jobId The job ID
     * @return FinalShortlistDTO with the shortlist
     */
    public FinalShortlistDTO getShortlist(String jobId) {
        logger.info("Getting shortlist for job: {}", jobId);
        
        // Try to get from cache first
        FinalShortlistDTO cachedShortlist = shortlistCacheService.getCachedShortlist(jobId);
        if (cachedShortlist != null) {
            cachedShortlist.setCached(true);
            logger.info("Retrieved cached shortlist for job: {}", jobId);
            return cachedShortlist;
        }
        
        // Generate new shortlist if not cached
        logger.info("No cached shortlist found for job: {}, generating new one", jobId);
        FinalShortlistDTO newShortlist = generateShortlist(jobId);
        newShortlist.setCached(false);
        return newShortlist;
    }

    /**
     * Gets cache statistics
     * @return Map with cache statistics
     */
    public Map<String, Object> getCacheStats() {
        return shortlistCacheService.getCacheStats();
    }

    /**
     * Clears cache for a specific job
     * @param jobId The job ID
     */
    public void clearJobCache(String jobId) {
        logger.info("Clearing cache for job: {}", jobId);
        shortlistCacheService.clearJobCache(jobId);
    }

    /**
     * Clears all cache
     */
    public void clearAllCache() {
        logger.info("Clearing all cache");
        shortlistCacheService.clearAllCache();
    }

    /**
     * Evaluates a single candidate for a job
     * @param candidate The candidate to evaluate
     * @param job The job to evaluate for
     * @return CandidateScoreDTO with evaluation results
     */
    private CandidateScoreDTO evaluateCandidate(User candidate, Job job) {
        logger.debug("Evaluating candidate: {} for job: {}", candidate.getId(), job.getId());
        
        // Get AI score (from existing job match or call external API)
        Double aiScore = getAIScore(candidate, job);
        
        // Get ATS score
        Double atsScore = atsProfileEvaluator.evaluateATSProfile(candidate);
        
        // Get profile completeness score
        Double profileScore = getProfileCompletenessScore(candidate);
        
        // Calculate final score using aggregator
        CandidateScoreDTO candidateScore = CandidateScoreDTO.builder()
                .candidateId(candidate.getId())
                .candidateName(candidate.getFirstName() + " " + candidate.getLastName())
                .build();
        candidateScore.setAiScore(aiScore);
        candidateScore.setAtsScore(atsScore);
        candidateScore.setProfileScore(profileScore);
        
        // Use aggregator to calculate final score
        scoreAggregator.calculateFinalScore(candidateScore, aiScoreWeight, atsScoreWeight, profileScoreWeight);
        
        return candidateScore;
    }

    /**
     * Gets AI score for a candidate
     * @param candidate The candidate
     * @param job The job
     * @return AI score (0.0 to 1.0)
     */
    private Double getAIScore(User candidate, Job job) {
        // Try to get existing score from job match
        JobMatch jobMatch = jobMatchRepository.findByJobIdAndUserId(job.getId(), candidate.getId());
        if (jobMatch != null) {
            return jobMatch.getScore();
        }
        
        // If no existing score, generate a mock score for demo purposes
        // In a real implementation, this would call an external AI API
        return Math.random() * 0.3 + 0.5; // Random score between 0.5 and 0.8
    }

    /**
     * Gets profile completeness score for a candidate
     * @param candidate The candidate
     * @return Profile completeness score (0.0 to 1.0)
     */
    private Double getProfileCompletenessScore(User candidate) {
        // Check if candidate has ATS profile
        if (candidate.getAtsProfile() != null) {
            boolean isComplete = candidate.getAtsProfile().isComplete();
            return isComplete ? 1.0 : 0.5; // Full score if complete, half if not
        }
        
        return 0.0; // No ATS profile
    }

    /**
     * Sorts candidates by final score in descending order
     * @param candidates List of candidates
     * @return Sorted list of candidates
     */
    private List<CandidateScoreDTO> sortCandidatesByScore(List<CandidateScoreDTO> candidates) {
        return candidates.stream()
                .sorted((c1, c2) -> Double.compare(c2.getFinalScore(), c1.getFinalScore()))
                .collect(Collectors.toList());
    }

    /**
     * Assigns ranks and states to candidates
     * @param candidates List of candidates
     */
    private void assignRanksAndStates(List<CandidateScoreDTO> candidates) {
        for (int i = 0; i < candidates.size(); i++) {
            CandidateScoreDTO candidate = candidates.get(i);
            candidate.setRank(i + 1);
            
            // Assign state based on rank and score
            if (i == 0 && candidate.getFinalScore() >= 0.8) {
                candidate.setState("PRESELECTED");
            } else if (i < 3 && candidate.getFinalScore() >= 0.7) {
                candidate.setState("SELECTED");
            } else {
                candidate.setState("REVIEW");
            }
        }
    }
}