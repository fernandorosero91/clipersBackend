package com.clipers.ai.integration.controller;

import com.clipers.ai.integration.dto.FinalShortlistDTO;
import com.clipers.ai.integration.service.MatchIntegrationService;
import com.clipers.clipers.exception.BusinessException;
import com.clipers.clipers.exception.ResourceNotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * REST controller for AI + ATS integration
 * Provides endpoints for generating and retrieving candidate shortlists
 */
@RestController
@RequestMapping("/integration")
@Tag(name = "AI + ATS Integration", description = "Endpoints for AI-powered candidate matching and shortlisting")
@Validated
public class MatchIntegrationController {

    private final MatchIntegrationService matchIntegrationService;

    @Autowired
    public MatchIntegrationController(MatchIntegrationService matchIntegrationService) {
        this.matchIntegrationService = matchIntegrationService;
    }

    /**
     * Generates a new shortlist for a job
     * @param jobId The job ID to generate shortlist for
     * @return FinalShortlistDTO with the generated shortlist
     */
    @PostMapping("/applications/{jobId}/select")
    @Operation(
            summary = "Generate candidate shortlist",
            description = "Generates a new shortlist of candidates for a specific job using AI + ATS integration"
    )
    public ResponseEntity<FinalShortlistDTO> generateShortlist(
            @Parameter(description = "ID of the job to generate shortlist for", required = true)
            @PathVariable String jobId) {
        
        try {
            FinalShortlistDTO shortlist = matchIntegrationService.generateShortlist(jobId);
            return ResponseEntity.ok(shortlist);
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException("Error generating shortlist for job: " + jobId, e);
        }
    }

    /**
     * Retrieves a shortlist from cache or generates a new one if not cached
     * @param jobId The job ID to retrieve shortlist for
     * @return FinalShortlistDTO with the shortlist
     */
    @GetMapping("/applications/{jobId}/shortlist")
    @Operation(
            summary = "Get candidate shortlist",
            description = "Retrieves a cached shortlist for a job or generates a new one if not available in cache"
    )
    public ResponseEntity<FinalShortlistDTO> getShortlist(
            @Parameter(description = "ID of the job to retrieve shortlist for", required = true)
            @PathVariable String jobId) {
        
        try {
            FinalShortlistDTO shortlist = matchIntegrationService.getShortlist(jobId);
            return ResponseEntity.ok(shortlist);
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException("Error retrieving shortlist for job: " + jobId, e);
        }
    }

    /**
     * Gets cache statistics
     * @return Map with cache statistics
     */
    @GetMapping("/cache/stats")
    @Operation(
            summary = "Get cache statistics",
            description = "Retrieves statistics about the cache performance and usage"
    )
    public ResponseEntity<Map<String, Object>> getCacheStats() {
        Map<String, Object> stats = matchIntegrationService.getCacheStats();
        return ResponseEntity.ok(stats);
    }

    /**
     * Clears cache for a specific job
     * @param jobId The job ID to clear cache for
     * @return Response indicating success
     */
    @DeleteMapping("/cache/{jobId}")
    @Operation(
            summary = "Clear job cache",
            description = "Clears the cached shortlist for a specific job"
    )
    public ResponseEntity<Void> clearJobCache(
            @Parameter(description = "ID of the job to clear cache for", required = true)
            @PathVariable String jobId) {
        
        matchIntegrationService.clearJobCache(jobId);
        return ResponseEntity.ok().build();
    }

    /**
     * Clears all cache
     * @return Response indicating success
     */
    @DeleteMapping("/cache")
    @Operation(
            summary = "Clear all cache",
            description = "Clears all cached shortlists"
    )
    public ResponseEntity<Void> clearAllCache() {
        matchIntegrationService.clearAllCache();
        return ResponseEntity.ok().build();
    }
}