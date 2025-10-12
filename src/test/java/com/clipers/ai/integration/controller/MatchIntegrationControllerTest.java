package com.clipers.ai.integration.controller;

import com.clipers.ai.integration.dto.FinalShortlistDTO;
import com.clipers.ai.integration.service.MatchIntegrationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for MatchIntegrationController
 */
@WebMvcTest(MatchIntegrationController.class)
public class MatchIntegrationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MatchIntegrationService matchIntegrationService;

    @Autowired
    private ObjectMapper objectMapper;

    private FinalShortlistDTO mockShortlist;

    @BeforeEach
    void setUp() {
        // Create a mock shortlist
        mockShortlist = new FinalShortlistDTO();
        mockShortlist.setJobId("job123");
        mockShortlist.setCached(false);
        mockShortlist.setGeneratedAt(java.time.LocalDateTime.now());
        
        // Add some mock candidates
        // In a real test, you would create proper CandidateScoreDTO objects
    }

    @Test
    void getShortlist_WhenShortlistExists_ShouldReturnShortlist() throws Exception {
        // Arrange
        when(matchIntegrationService.getShortlist(anyString())).thenReturn(mockShortlist);

        // Act & Assert
        mockMvc.perform(get("/api/integration/shortlist/job123")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.jobId").value("job123"))
                .andExpect(jsonPath("$.cached").value(false));
    }

    @Test
    void generateShortlist_WhenValidJobId_ShouldReturnGeneratedShortlist() throws Exception {
        // Arrange
        when(matchIntegrationService.generateShortlist(anyString())).thenReturn(mockShortlist);

        // Act & Assert
        mockMvc.perform(post("/api/integration/shortlist/generate/job123")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.jobId").value("job123"))
                .andExpect(jsonPath("$.cached").value(false));
    }

    @Test
    void getCacheStats_ShouldReturnCacheStatistics() throws Exception {
        // Arrange
        Map<String, Object> mockStats = new HashMap<>();
        mockStats.put("totalKeys", 10);
        mockStats.put("memoryUsage", "1MB");
        mockStats.put("hitRate", 0.95);
        
        when(matchIntegrationService.getCacheStats()).thenReturn(mockStats);

        // Act & Assert
        mockMvc.perform(get("/api/integration/cache/stats")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalKeys").value(10))
                .andExpect(jsonPath("$.memoryUsage").value("1MB"))
                .andExpect(jsonPath("$.hitRate").value(0.95));
    }

    @Test
    void clearJobCache_WhenValidJobId_ShouldReturnSuccess() throws Exception {
        // Act & Assert
        mockMvc.perform(delete("/api/integration/cache/job123")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("Cache cleared for job: job123"));
    }

    @Test
    void clearAllCache_ShouldReturnSuccess() throws Exception {
        // Act & Assert
        mockMvc.perform(delete("/api/integration/cache")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("All cache cleared"));
    }
}