package com.clipers.ai.integration.service;

import com.clipers.ai.integration.dto.CandidateScoreDTO;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * Service for aggregating scores from different sources
 * Implements Builder pattern for flexible configuration
 */
@Service
public class ScoreAggregator {

    private double aiScoreWeight;
    private double atsScoreWeight;
    private double profileScoreWeight;
    private boolean normalizeScores;
    private boolean applyThreshold;
    private double threshold;
    private boolean capScores;
    private boolean roundScores;
    private int decimalPlaces;

    /**
     * Builder for ScoreAggregator
     */
    public static class ScoreAggregatorBuilder {
        private double aiScoreWeight = 0.6;
        private double atsScoreWeight = 0.3;
        private double profileScoreWeight = 0.1;
        private boolean normalizeScores = true;
        private boolean applyThreshold = false;
        private double threshold = 0.5;
        private boolean capScores = true;
        private boolean roundScores = true;
        private int decimalPlaces = 2;

        public ScoreAggregatorBuilder withAiScoreWeight(double aiScoreWeight) {
            this.aiScoreWeight = aiScoreWeight;
            return this;
        }

        public ScoreAggregatorBuilder withAtsScoreWeight(double atsScoreWeight) {
            this.atsScoreWeight = atsScoreWeight;
            return this;
        }

        public ScoreAggregatorBuilder withProfileScoreWeight(double profileScoreWeight) {
            this.profileScoreWeight = profileScoreWeight;
            return this;
        }

        public ScoreAggregatorBuilder withNormalization(boolean normalizeScores) {
            this.normalizeScores = normalizeScores;
            return this;
        }

        public ScoreAggregatorBuilder withThreshold(double threshold) {
            this.applyThreshold = true;
            this.threshold = threshold;
            return this;
        }

        public ScoreAggregatorBuilder withCapping(boolean capScores) {
            this.capScores = capScores;
            return this;
        }

        public ScoreAggregatorBuilder withRounding(boolean roundScores) {
            this.roundScores = roundScores;
            return this;
        }

        public ScoreAggregatorBuilder withDecimalPlaces(int decimalPlaces) {
            this.decimalPlaces = decimalPlaces;
            return this;
        }

        public ScoreAggregator build() {
            // Validate weights sum to 1.0
            double totalWeight = aiScoreWeight + atsScoreWeight + profileScoreWeight;
            if (Math.abs(totalWeight - 1.0) > 0.001) {
                throw new IllegalArgumentException("Weights must sum to 1.0");
            }

            // Validate threshold
            if (applyThreshold && (threshold < 0.0 || threshold > 1.0)) {
                throw new IllegalArgumentException("Threshold must be between 0.0 and 1.0");
            }

            // Validate decimal places
            if (decimalPlaces < 0) {
                throw new IllegalArgumentException("Decimal places cannot be negative");
            }

            ScoreAggregator aggregator = new ScoreAggregator();
            aggregator.aiScoreWeight = aiScoreWeight;
            aggregator.atsScoreWeight = atsScoreWeight;
            aggregator.profileScoreWeight = profileScoreWeight;
            aggregator.normalizeScores = normalizeScores;
            aggregator.applyThreshold = applyThreshold;
            aggregator.threshold = threshold;
            aggregator.capScores = capScores;
            aggregator.roundScores = roundScores;
            aggregator.decimalPlaces = decimalPlaces;
            return aggregator;
        }
    }

    /**
     * Calculates final score for a candidate using configured weights
     * @param candidateScore Candidate with individual scores
     * @return Final score (0.0 to 1.0)
     */
    public double calculateFinalScore(CandidateScoreDTO candidateScore) {
        return calculateFinalScore(candidateScore, aiScoreWeight, atsScoreWeight, profileScoreWeight);
    }

    /**
     * Calculates final score for a candidate using custom weights
     * @param candidateScore Candidate with individual scores
     * @param aiWeight Weight for AI score
     * @param atsWeight Weight for ATS score
     * @param profileWeight Weight for profile score
     * @return Final score (0.0 to 1.0)
     */
    public double calculateFinalScore(CandidateScoreDTO candidateScore, 
                                     double aiWeight, double atsWeight, double profileWeight) {
        // Validate weights
        double totalWeight = aiWeight + atsWeight + profileWeight;
        if (Math.abs(totalWeight - 1.0) > 0.001) {
            throw new IllegalArgumentException("Weights must sum to 1.0");
        }

        // Get individual scores
        Double aiScore = candidateScore.getAiScore();
        Double atsScore = candidateScore.getAtsScore();
        Double profileScore = candidateScore.getProfileScore();

        // Check for null scores
        if (aiScore == null || atsScore == null || profileScore == null) {
            logger().warn("One or more scores are null for candidate: {}. Setting final score to 0.0", candidateScore.getCandidateId());
            candidateScore.setFinalScore(0.0);
            return 0.0;
        }

        // Normalize scores if enabled
        if (normalizeScores) {
            aiScore = normalizeScore(aiScore);
            atsScore = normalizeScore(atsScore);
            profileScore = normalizeScore(profileScore);
        }

        // Calculate weighted sum
        double finalScore = (aiScore * aiWeight) + (atsScore * atsWeight) + (profileScore * profileWeight);

        // Apply threshold if enabled
        if (applyThreshold && finalScore < threshold) {
            finalScore = 0.0;
        }

        // Cap scores if enabled
        if (capScores) {
            finalScore = Math.min(finalScore, 1.0);
        }

        // Round scores if enabled
        if (roundScores) {
            finalScore = roundToDecimalPlaces(finalScore, decimalPlaces);
        }

        // Set final score on candidate
        candidateScore.setFinalScore(finalScore);
        return finalScore;
    }

    /**
     * Normalizes a score to be between 0.0 and 1.0
     * @param score The score to normalize
     * @return Normalized score
     */
    public double normalizeScore(double score) {
        // In production, you might want more sophisticated normalization
        if (score < 0.0) {
            return 0.0;
        } else if (score > 1.0) {
            return 1.0;
        }
        
        return score;
    }

    /**
     * Rounds a number to specified decimal places
     * @param value The value to round
     * @param places Number of decimal places
     * @return Rounded value
     */
    public double roundToDecimalPlaces(double value, int places) {
        if (places < 0) places = 0;
        
        double factor = Math.pow(10, places);
        return Math.round(value * factor) / factor;
    }

    /**
     * Creates a new builder instance
     * @return A new ScoreAggregatorBuilder
     */
    public static ScoreAggregatorBuilder builder() {
        return new ScoreAggregatorBuilder();
    }

    /**
     * Gets the current configuration as a map
     * @return Map with current configuration
     */
    public Map<String, Object> getConfiguration() {
        Map<String, Object> config = new HashMap<>();
        config.put("aiScoreWeight", aiScoreWeight);
        config.put("atsScoreWeight", atsScoreWeight);
        config.put("profileScoreWeight", profileScoreWeight);
        config.put("normalizeScores", normalizeScores);
        config.put("applyThreshold", applyThreshold);
        config.put("threshold", threshold);
        config.put("capScores", capScores);
        config.put("roundScores", roundScores);
        config.put("decimalPlaces", decimalPlaces);
        return config;
    }

    /**
     * Validates if the current configuration is valid
     * @return True if valid, false otherwise
     */
    public boolean isValid() {
        // Check if weights sum to 1.0
        double totalWeight = aiScoreWeight + atsScoreWeight + profileScoreWeight;
        if (Math.abs(totalWeight - 1.0) > 0.001) {
            return false;
        }
        
        // Check if threshold is valid
        if (applyThreshold && (threshold < 0.0 || threshold > 1.0)) {
            return false;
        }
        
        // Check if decimal places is valid
        if (decimalPlaces < 0) {
            return false;
        }
        
        return true;
    }

    /**
     * Creates a copy of this aggregator with modified weights
     * @param aiWeight New AI score weight
     * @param atsWeight New ATS score weight
     * @param profileWeight New profile score weight
     * @return New ScoreAggregator with updated weights
     */
    public ScoreAggregator withWeights(double aiWeight, double atsWeight, double profileWeight) {
        return builder()
                .withAiScoreWeight(aiWeight)
                .withAtsScoreWeight(atsWeight)
                .withProfileScoreWeight(profileWeight)
                .withNormalization(normalizeScores)
                .withThreshold(applyThreshold ? threshold : 0.0)
                .withCapping(capScores)
                .withRounding(roundScores)
                .withDecimalPlaces(decimalPlaces)
                .build();
    }

    // Getters for configuration values
    public double getAiScoreWeight() { return aiScoreWeight; }
    public double getAtsScoreWeight() { return atsScoreWeight; }
    public double getProfileScoreWeight() { return profileScoreWeight; }
    public boolean isNormalizeScores() { return normalizeScores; }
    public boolean isApplyThreshold() { return applyThreshold; }
    public double getThreshold() { return threshold; }
    public boolean isCapScores() { return capScores; }
    public boolean isRoundScores() { return roundScores; }
    public int getDecimalPlaces() { return decimalPlaces; }
    
    private org.slf4j.Logger logger() {
        return org.slf4j.LoggerFactory.getLogger(ScoreAggregator.class);
    }
}