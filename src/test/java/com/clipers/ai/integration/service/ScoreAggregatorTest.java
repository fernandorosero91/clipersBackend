package com.clipers.ai.integration.service;

import com.clipers.ai.integration.dto.CandidateScoreDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for ScoreAggregator
 */
@ExtendWith(MockitoExtension.class)
class ScoreAggregatorTest {

    private ScoreAggregator aggregator;

    @BeforeEach
    void setUp() {
        aggregator = new ScoreAggregator();
    }

    @Test
    void testCalculateFinalScoreWithNullScores() {
        CandidateScoreDTO candidate = new CandidateScoreDTO();
        candidate.setCandidateId("123");
        candidate.setCandidateName("John Doe");
        
        // Set some scores to null
        candidate.setAiScore(0.8);
        candidate.setAtsScore(null);
        candidate.setProfileScore(0.6);
        
        double finalScore = aggregator.calculateFinalScore(candidate);
        assertEquals(0.0, finalScore, "Final score should be 0.0 when any score is null");
    }

    @Test
    void testCalculateFinalScoreWithValidScores() {
        CandidateScoreDTO candidate = new CandidateScoreDTO();
        candidate.setCandidateId("123");
        candidate.setCandidateName("John Doe");
        candidate.setAiScore(0.8);
        candidate.setAtsScore(0.7);
        candidate.setProfileScore(0.9);
        
        double finalScore = aggregator.calculateFinalScore(candidate);
        assertEquals(0.8, finalScore, 0.001, "Final score should be weighted average");
    }

    @Test
    void testCalculateFinalScoreWithCustomWeights() {
        CandidateScoreDTO candidate = new CandidateScoreDTO();
        candidate.setCandidateId("123");
        candidate.setCandidateName("John Doe");
        candidate.setAiScore(0.8);
        candidate.setAtsScore(0.7);
        candidate.setProfileScore(0.9);
        
        double aiWeight = 0.4;
        double atsWeight = 0.4;
        double profileWeight = 0.2;
        
        double finalScore = aggregator.calculateFinalScore(candidate, aiWeight, atsWeight, profileWeight);
        double expectedScore = (0.8 * aiWeight) + (0.7 * atsWeight) + (0.9 * profileWeight);
        assertEquals(expectedScore, finalScore, 0.001, "Final score should match weighted average");
    }

    @Test
    void testCalculateFinalScoreWithWeightsNotSummingToOne() {
        CandidateScoreDTO candidate = new CandidateScoreDTO();
        candidate.setCandidateId("123");
        candidate.setCandidateName("John Doe");
        candidate.setAiScore(0.8);
        candidate.setAtsScore(0.7);
        candidate.setProfileScore(0.9);
        
        double aiWeight = 0.5;
        double atsWeight = 0.5;
        double profileWeight = 0.5; // Sum is 1.5, not 1.0
        
        assertThrows(IllegalArgumentException.class, () -> {
            aggregator.calculateFinalScore(candidate, aiWeight, atsWeight, profileWeight);
        }, "Should throw exception when weights don't sum to 1.0");
    }

    @Test
    void testNormalizeScore() {
        // Test with score below 0
        double normalized = aggregator.normalizeScore(-0.5);
        assertEquals(0.0, normalized, "Score below 0 should be normalized to 0.0");
        
        // Test with score above 1
        normalized = aggregator.normalizeScore(1.5);
        assertEquals(1.0, normalized, "Score above 1 should be normalized to 1.0");
        
        // Test with score within range
        normalized = aggregator.normalizeScore(0.7);
        assertEquals(0.7, normalized, "Score within range should remain unchanged");
    }

    @Test
    void testRoundToDecimalPlaces() {
        // Test with 0 decimal places
        double rounded = aggregator.roundToDecimalPlaces(0.8765, 0);
        assertEquals(1.0, rounded, "Should round to nearest whole number");
        
        // Test with 2 decimal places
        rounded = aggregator.roundToDecimalPlaces(0.8765, 2);
        assertEquals(0.88, rounded, "Should round to 2 decimal places");
        
        // Test with negative decimal places
        rounded = aggregator.roundToDecimalPlaces(0.8765, -1);
        assertEquals(0.9, rounded, "Should handle negative decimal places");
    }

    @Test
    void testBuilderWithValidWeights() {
        ScoreAggregator customAggregator = ScoreAggregator.builder()
                .withAiScoreWeight(0.5)
                .withAtsScoreWeight(0.3)
                .withProfileScoreWeight(0.2)
                .build();
        
        assertNotNull(customAggregator, "Builder should create a valid aggregator");
        assertTrue(customAggregator.isValid(), "Aggregator should be valid with correct weights");
    }

    @Test
    void testBuilderWithInvalidWeights() {
        assertThrows(IllegalArgumentException.class, () -> {
            ScoreAggregator.builder()
                    .withAiScoreWeight(0.5)
                    .withAtsScoreWeight(0.5)
                    .withProfileScoreWeight(0.5) // Sum is 1.5, not 1.0
                    .build();
        }, "Builder should throw exception with invalid weights");
    }

    @Test
    void testBuilderWithThreshold() {
        ScoreAggregator customAggregator = ScoreAggregator.builder()
                .withAiScoreWeight(0.6)
                .withAtsScoreWeight(0.3)
                .withProfileScoreWeight(0.1)
                .withThreshold(0.7)
                .build();
        
        assertTrue(customAggregator.isApplyThreshold(), "Aggregator should have threshold enabled");
        assertEquals(0.7, customAggregator.getThreshold(), "Threshold should be set correctly");
    }

    @Test
    void testBuilderWithInvalidThreshold() {
        assertThrows(IllegalArgumentException.class, () -> {
            ScoreAggregator.builder()
                    .withAiScoreWeight(0.6)
                    .withAtsScoreWeight(0.3)
                    .withProfileScoreWeight(0.1)
                    .withThreshold(1.5) // Invalid threshold
                    .build();
        }, "Builder should throw exception with invalid threshold");
    }

    @Test
    void testGetConfiguration() {
        ScoreAggregator customAggregator = ScoreAggregator.builder()
                .withAiScoreWeight(0.5)
                .withAtsScoreWeight(0.3)
                .withProfileScoreWeight(0.2)
                .withNormalization(false)
                .withThreshold(0.6)
                .withCapping(false)
                .withRounding(false)
                .withDecimalPlaces(3)
                .build();
        
        Map<String, Object> config = customAggregator.getConfiguration();
        
        assertEquals(0.5, config.get("aiScoreWeight"));
        assertEquals(0.3, config.get("atsScoreWeight"));
        assertEquals(0.2, config.get("profileScoreWeight"));
        assertEquals(false, config.get("normalizeScores"));
        assertEquals(0.6, config.get("threshold"));
        assertEquals(false, config.get("capScores"));
        assertEquals(false, config.get("roundScores"));
        assertEquals(3, config.get("decimalPlaces"));
    }

    @Test
    void testIsValid() {
        // Valid aggregator
        ScoreAggregator validAggregator = ScoreAggregator.builder()
                .withAiScoreWeight(0.6)
                .withAtsScoreWeight(0.3)
                .withProfileScoreWeight(0.1)
                .build();
        
        assertTrue(validAggregator.isValid(), "Aggregator with valid weights should be valid");
        
        // Invalid aggregator (weights don't sum to 1.0)
        ScoreAggregator invalidAggregator = ScoreAggregator.builder()
                .withAiScoreWeight(0.5)
                .withAtsScoreWeight(0.5)
                .withProfileScoreWeight(0.5)
                .build();
        
        assertFalse(invalidAggregator.isValid(), "Aggregator with invalid weights should be invalid");
    }

    @Test
    void testWithWeights() {
        ScoreAggregator original = ScoreAggregator.builder()
                .withAiScoreWeight(0.6)
                .withAtsScoreWeight(0.3)
                .withProfileScoreWeight(0.1)
                .build();
        
        ScoreAggregator modified = original.withWeights(0.4, 0.4, 0.2);
        
        assertEquals(0.4, modified.getAiScoreWeight(), "AI weight should be updated");
        assertEquals(0.4, modified.getAtsScoreWeight(), "ATS weight should be updated");
        assertEquals(0.2, modified.getProfileScoreWeight(), "Profile weight should be updated");
    }

    @Test
    void testCalculateFinalScoreWithCapping() {
        CandidateScoreDTO candidate = new CandidateScoreDTO();
        candidate.setCandidateId("123");
        candidate.setCandidateName("John Doe");
        candidate.setAiScore(1.2); // Above 1.0
        candidate.setAtsScore(1.1); // Above 1.0
        candidate.setProfileScore(1.3); // Above 1.0
        
        double finalScore = aggregator.calculateFinalScore(candidate);
        assertEquals(1.0, finalScore, "Final score should be capped at 1.0");
    }

    @Test
    void testCalculateFinalScoreWithRounding() {
        CandidateScoreDTO candidate = new CandidateScoreDTO();
        candidate.setCandidateId("123");
        candidate.setCandidateName("John Doe");
        candidate.setAiScore(0.8765);
        candidate.setAtsScore(0.7654);
        candidate.setProfileScore(0.6543);
        
        double finalScore = aggregator.calculateFinalScore(candidate);
        assertEquals(0.81, finalScore, 0.01, "Final score should be rounded to 2 decimal places");
    }

    @Test
    void testCalculateFinalScoreWithThreshold() {
        ScoreAggregator aggregatorWithThreshold = ScoreAggregator.builder()
                .withAiScoreWeight(0.6)
                .withAtsScoreWeight(0.3)
                .withProfileScoreWeight(0.1)
                .withThreshold(0.7)
                .build();
        
        CandidateScoreDTO candidate = new CandidateScoreDTO();
        candidate.setCandidateId("123");
        candidate.setCandidateName("John Doe");
        candidate.setAiScore(0.5);
        candidate.setAtsScore(0.5);
        candidate.setProfileScore(0.5);
        
        double finalScore = aggregatorWithThreshold.calculateFinalScore(candidate);
        assertEquals(0.0, finalScore, "Final score should be 0.0 when below threshold");
    }
}