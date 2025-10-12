package com.clipers.ai.integration.service;

import com.clipers.ai.integration.dto.CandidateScoreDTO;
import com.clipers.ai.integration.dto.FinalShortlistDTO;
import com.clipers.clipers.entity.Job;
import com.clipers.clipers.entity.JobMatch;
import com.clipers.clipers.entity.User;
import com.clipers.clipers.exception.ResourceNotFoundException;
import com.clipers.clipers.repository.JobMatchRepository;
import com.clipers.clipers.repository.JobRepository;
import com.clipers.clipers.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.CacheManager;
import org.springframework.data.redis.cache.RedisCacheManager;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Unit tests for MatchIntegrationService
 */
@ExtendWith(MockitoExtension.class)
class MatchIntegrationServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private JobRepository jobRepository;

    @Mock
    private JobMatchRepository jobMatchRepository;

    @Mock
    private ATSProfileEvaluator atsProfileEvaluator;

    @Mock
    private ScoreAggregator scoreAggregator;

    @Mock
    private ShortlistCacheService shortlistCacheService;

    @Mock
    private CacheManager cacheManager;

    @InjectMocks
    private MatchIntegrationService matchIntegrationService;

    private Job testJob;
    private User testUser;
    private JobMatch testJobMatch;

    @BeforeEach
    void setUp() {
        // Setup test data
        testJob = new Job();
        testJob.setId("job123");
        testJob.setTitle("Software Engineer");
        testJob.setDescription("We are looking for a skilled software engineer...");

        testUser = new User();
        testUser.setId("user123");
        testUser.setFirstName("John");
        testUser.setLastName("Doe");
        testUser.setEmail("john.doe@example.com");

        testJobMatch = new JobMatch();
        testJobMatch.setJobId("job123");
        testJobMatch.setUserId("user123");
        testJobMatch.setScore(0.85);
    }

    @Test
    void generateShortlist_WhenJobExistsAndCandidatesExist_ShouldReturnShortlist() {
        // Arrange
        when(jobRepository.findById("job123")).thenReturn(java.util.Optional.of(testJob));
        when(userRepository.findAll()).thenReturn(Arrays.asList(testUser));
        when(jobMatchRepository.findByJobIdAndUserId("job123", "user123")).thenReturn(testJobMatch);

        // Act
        FinalShortlistDTO result = matchIntegrationService.generateShortlist("job123");

        // Assert
        assertNotNull(result);
        assertEquals("job123", result.getJobId());
        assertFalse(result.isCached());
        assertEquals(1, result.getCandidates().size());
        assertEquals("John Doe", result.getCandidates().get(0).getCandidateName());
    }

    @Test
    void generateShortlist_WhenJobDoesNotExist_ShouldThrowException() {
        // Arrange
        when(jobRepository.findById("nonexistent")).thenReturn(java.util.Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            matchIntegrationService.generateShortlist("nonexistent");
        });
    }

    @Test
    void generateShortlist_WhenNoCandidatesExist_ShouldReturnEmptyShortlist() {
        // Arrange
        when(jobRepository.findById("job123")).thenReturn(java.util.Optional.of(testJob));
        when(userRepository.findAll()).thenReturn(Collections.emptyList());

        // Act
        FinalShortlistDTO result = matchIntegrationService.generateShortlist("job123");

        // Assert
        assertNotNull(result);
        assertEquals("job123", result.getJobId());
        assertTrue(result.getCandidates().isEmpty());
    }

    @Test
    void getShortlist_WhenCachedShortlistExists_ShouldReturnCachedShortlist() {
        // Arrange
        FinalShortlistDTO cachedShortlist = new FinalShortlistDTO();
        cachedShortlist.setJobId("job123");
        cachedShortlist.setCached(true);
        
        when(shortlistCacheService.getCachedShortlist("job123")).thenReturn(cachedShortlist);

        // Act
        FinalShortlistDTO result = matchIntegrationService.getShortlist("job123");

        // Assert
        assertNotNull(result);
        assertEquals("job123", result.getJobId());
        assertTrue(result.isCached());
    }

    @Test
    void getShortlist_WhenNoCachedShortlistExists_ShouldGenerateNewShortlist() {
        // Arrange
        when(jobRepository.findById("job123")).thenReturn(java.util.Optional.of(testJob));
        when(userRepository.findAll()).thenReturn(Arrays.asList(testUser));
        when(jobMatchRepository.findByJobIdAndUserId("job123", "user123")).thenReturn(testJobMatch);
        when(shortlistCacheService.getCachedShortlist("job123")).thenReturn(null);

        // Act
        FinalShortlistDTO result = matchIntegrationService.getShortlist("job123");

        // Assert
        assertNotNull(result);
        assertEquals("job123", result.getJobId());
        assertFalse(result.isCached());
    }

    @Test
    void getCacheStats_ShouldReturnCacheStatistics() {
        // Arrange
        Map<String, Object> mockStats = Map.of(
            "totalKeys", 10,
            "memoryUsage", "1MB",
            "hitRate", 0.95
        );
        
        when(shortlistCacheService.getCacheStats()).thenReturn(mockStats);

        // Act
        Map<String, Object> result = matchIntegrationService.getCacheStats();

        // Assert
        assertNotNull(result);
        assertEquals(10, result.get("totalKeys"));
        assertEquals("1MB", result.get("memoryUsage"));
        assertEquals(0.95, result.get("hitRate"));
    }

    @Test
    void clearJobCache_ShouldClearCacheForSpecificJob() {
        // Act
        matchIntegrationService.clearJobCache("job123");

        // Assert
        verify(shortlistCacheService, times(1)).clearJobCache("job123");
    }

    @Test
    void clearAllCache_ShouldClearAllCache() {
        // Act
        matchIntegrationService.clearAllCache();

        // Assert
        verify(shortlistCacheService, times(1)).clearAllCache();
    }

    @Test
    void evaluateCandidate_WhenValidCandidateAndJob_ShouldReturnScoredCandidate() {
        // Arrange
        when(jobMatchRepository.findByJobIdAndUserId("job123", "user123")).thenReturn(testJobMatch);

        // Act
        CandidateScoreDTO result = matchIntegrationService.evaluateCandidate(testUser, testJob);

        // Assert
        assertNotNull(result);
        assertEquals("user123", result.getCandidateId());
        assertEquals("John Doe", result.getCandidateName());
        assertEquals(0.85, result.getAiScore());
    }

    @Test
    void evaluateCandidate_WhenNoExistingScore_ShouldGenerateRandomScore() {
        // Arrange
        when(jobMatchRepository.findByJobIdAndUserId("job123", "user123")).thenReturn(null);

        // Act
        CandidateScoreDTO result = matchIntegrationService.evaluateCandidate(testUser, testJob);

        // Assert
        assertNotNull(result);
        assertEquals("user123", result.getCandidateId());
        assertEquals("John Doe", result.getCandidateName());
        assertTrue(result.getAiScore() >= 0.5 && result.getAiScore() <= 0.8);
    }
}