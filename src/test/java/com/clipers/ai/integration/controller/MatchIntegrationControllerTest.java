package com.clipers.ai.integration.controller;

import com.clipers.ai.integration.dto.FinalShortlistDTO;
import com.clipers.ai.integration.service.MatchIntegrationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit tests for MatchIntegrationController
 */
@ExtendWith(MockitoExtension.class)
class MatchIntegrationControllerTest {

    private MockMvc mockMvc;

    @Mock
    private MatchIntegrationService matchIntegrationService;

    @InjectMocks
    private MatchIntegrationController controller;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void testGenerateShortlist() throws Exception {
        String jobId = "job123";
        FinalShortlistDTO shortlist = createMockShortlist(jobId);
        
        when(matchIntegrationService.generateShortlist(jobId)).thenReturn(shortlist);
        
        mockMvc.perform(post("/integration/applications/{jobId}/select", jobId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.jobId").value(jobId))
                .andExpect(jsonPath("$.cached").value(false));
        
        verify(matchIntegrationService).generateShortlist(jobId);
    }

    @Test
    void testGetShortlist() throws Exception {
        String jobId = "job123";
        FinalShortlistDTO shortlist = createMockShortlist(jobId);
        
        when(matchIntegrationService.getShortlist(jobId)).thenReturn(shortlist);
        
        mockMvc.perform(get("/integration/applications/{jobId}/shortlist", jobId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.jobId").value(jobId))
                .andExpect(jsonPath("$.cached").value(false));
        
        verify(matchIntegrationService).getShortlist(jobId);
    }

    @Test
    void testGetShortlistCached() throws Exception {
        String jobId = "job123";
        FinalShortlistDTO shortlist = createMockShortlist(jobId);
        shortlist.setCached(true);
        
        when(matchIntegrationService.getShortlist(jobId)).thenReturn(shortlist);
        
        mockMvc.perform(get("/integration/applications/{jobId}/shortlist", jobId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.jobId").value(jobId))
                .andExpect(jsonPath("$.cached").value(true));
        
        verify(matchIntegrationService).getShortlist(jobId);
    }

    @Test
    void testGetCacheStats() throws Exception {
        Map<String, Object> stats = Map.of(
                "enabled", true,
                "hitCount", 10,
                "missCount", 5,
                "totalRequests", 15,
                "hitRate", "66.67%",
                "cacheSize", 3
        );
        
        when(matchIntegrationService.getCacheStats()).thenReturn(stats);
        
        mockMvc.perform(get("/integration/cache/stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.enabled").value(true))
                .andExpect(jsonPath("$.hitCount").value(10))
                .andExpect(jsonPath("$.missCount").value(5))
                .andExpect(jsonPath("$.totalRequests").value(15))
                .andExpect(jsonPath("$.hitRate").value("66.67%"))
                .andExpect(jsonPath("$.cacheSize").value(3));
        
        verify(matchIntegrationService).getCacheStats();
    }

    @Test
    void testClearJobCache() throws Exception {
        String jobId = "job123";
        
        mockMvc.perform(delete("/integration/cache/{jobId}", jobId))
                .andExpect(status().isOk());
        
        verify(matchIntegrationService).clearJobCache(jobId);
    }

    @Test
    void testClearAllCache() throws Exception {
        mockMvc.perform(delete("/integration/cache"))
                .andExpect(status().isOk());
        
        verify(matchIntegrationService).clearAllCache();
    }

    @Test
    void testGenerateShortlistWithInvalidJobId() throws Exception {
        String jobId = "";
        
        mockMvc.perform(post("/integration/applications/{jobId}/select", jobId))
                .andExpect(status().isBadRequest());
        
        verify(matchIntegrationService, never()).generateShortlist(anyString());
    }

    @Test
    void testGetShortlistWithInvalidJobId() throws Exception {
        String jobId = "   ";
        
        mockMvc.perform(get("/integration/applications/{jobId}/shortlist", jobId))
                .andExpect(status().isBadRequest());
        
        verify(matchIntegrationService, never()).getShortlist(anyString());
    }

    @Test
    void testGenerateShortlistWithServiceException() throws Exception {
        String jobId = "job123";
        
        when(matchIntegrationService.generateShortlist(jobId))
                .thenThrow(new RuntimeException("Service error"));
        
        mockMvc.perform(post("/integration/applications/{jobId}/select", jobId))
                .andExpect(status().is5xxServerError());
        
        verify(matchIntegrationService).generateShortlist(jobId);
    }

    @Test
    void testGetShortlistWithServiceException() throws Exception {
        String jobId = "job123";
        
        when(matchIntegrationService.getShortlist(jobId))
                .thenThrow(new RuntimeException("Service error"));
        
        mockMvc.perform(get("/integration/applications/{jobId}/shortlist", jobId))
                .andExpect(status().is5xxServerError());
        
        verify(matchIntegrationService).getShortlist(jobId);
    }

    @Test
    void testClearJobCacheWithServiceException() throws Exception {
        String jobId = "job123";
        
        doThrow(new RuntimeException("Service error"))
                .when(matchIntegrationService).clearJobCache(jobId);
        
        mockMvc.perform(delete("/integration/cache/{jobId}", jobId))
                .andExpect(status().is5xxServerError());
        
        verify(matchIntegrationService).clearJobCache(jobId);
    }

    @Test
    void testClearAllCacheWithServiceException() throws Exception {
        doThrow(new RuntimeException("Service error"))
                .when(matchIntegrationService).clearAllCache();
        
        mockMvc.perform(delete("/integration/cache"))
                .andExpect(status().is5xxServerError());
        
        verify(matchIntegrationService).clearAllCache();
    }

    @Test
    void testGetCacheStatsWithServiceException() throws Exception {
        when(matchIntegrationService.getCacheStats())
                .thenThrow(new RuntimeException("Service error"));
        
        mockMvc.perform(get("/integration/cache/stats"))
                .andExpect(status().is5xxServerError());
        
        verify(matchIntegrationService).getCacheStats();
    }

    /**
     * Helper method to create a mock shortlist for testing
     */
    private FinalShortlistDTO createMockShortlist(String jobId) {
        FinalShortlistDTO shortlist = new FinalShortlistDTO();
        shortlist.setJobId(jobId);
        shortlist.setCached(false);
        
        // Add some mock candidates
        // In a real test, you might create proper CandidateScoreDTO objects
        // For simplicity, we'll just set the list to empty
        shortlist.setCandidates(List.of());
        
        return shortlist;
    }
}