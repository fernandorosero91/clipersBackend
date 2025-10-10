package com.clipers.ai.integration.service;

import com.clipers.ai.integration.dto.FinalShortlistDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Service for caching shortlists using Redis with decorator pattern
 * Provides statistics, logging, and TTL management
 */
@Service
public class ShortlistCacheService {

    private static final Logger logger = LoggerFactory.getLogger(ShortlistCacheService.class);

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Value("${cache.ttl:1800}")
    private long cacheTtl;

    private boolean cacheEnabled = true;
    private final AtomicLong hitCount = new AtomicLong(0);
    private final AtomicLong missCount = new AtomicLong(0);
    private final Map<String, LocalDateTime> lastAccessTimes = new ConcurrentHashMap<>();

    /**
     * Cache component interface
     */
    public interface CacheComponent {
        FinalShortlistDTO get(String jobId);
        void put(String jobId, FinalShortlistDTO shortlist, long ttl);
        void evict(String jobId);
        void clear();
        boolean containsKey(String jobId);
    }

    /**
     * Redis cache implementation
     */
    public static class RedisCache implements CacheComponent {
        private final RedisTemplate<String, Object> redisTemplate;

        public RedisCache(RedisTemplate<String, Object> redisTemplate) {
            this.redisTemplate = redisTemplate;
        }

        @Override
        public FinalShortlistDTO get(String jobId) {
            try {
                return (FinalShortlistDTO) redisTemplate.opsForValue().get(jobId);
            } catch (Exception e) {
                logger.error("Error getting cache for job {}: {}", jobId, e.getMessage());
                return null;
            }
        }

        @Override
        public void put(String jobId, FinalShortlistDTO shortlist, long ttl) {
            try {
                redisTemplate.opsForValue().set(jobId, shortlist, ttl, java.util.concurrent.TimeUnit.SECONDS);
            } catch (Exception e) {
                logger.error("Error putting cache for job {}: {}", jobId, e.getMessage());
            }
        }

        @Override
        public void evict(String jobId) {
            try {
                redisTemplate.delete(jobId);
            } catch (Exception e) {
                logger.error("Error evicting cache for job {}: {}", jobId, e.getMessage());
            }
        }

        @Override
        public void clear() {
            try {
                // In production, you might want to use a pattern to delete only cache keys
                // For simplicity, we'll just log the action
                logger.warn("Clearing all cache - this would delete all keys in production");
            } catch (Exception e) {
                logger.error("Error clearing cache: {}", e.getMessage());
            }
        }

        @Override
        public boolean containsKey(String jobId) {
            try {
                return redisTemplate.hasKey(jobId);
            } catch (Exception e) {
                logger.error("Error checking cache key for job {}: {}", jobId, e.getMessage());
                return false;
            }
        }
    }

    /**
     * Statistics decorator for cache component
     */
    public static class StatisticsDecorator implements CacheComponent {
        private final CacheComponent delegate;
        private final AtomicLong hitCount = new AtomicLong(0);
        private final AtomicLong missCount = new AtomicLong(0);

        public StatisticsDecorator(CacheComponent delegate) {
            this.delegate = delegate;
        }

        @Override
        public FinalShortlistDTO get(String jobId) {
            FinalShortlistDTO result = delegate.get(jobId);
            if (result != null) {
                hitCount.incrementAndGet();
            } else {
                missCount.incrementAndGet();
            }
            return result;
        }

        @Override
        public void put(String jobId, FinalShortlistDTO shortlist, long ttl) {
            delegate.put(jobId, shortlist, ttl);
        }

        @Override
        public void evict(String jobId) {
            delegate.evict(jobId);
        }

        @Override
        public void clear() {
            delegate.clear();
        }

        @Override
        public boolean containsKey(String jobId) {
            return delegate.containsKey(jobId);
        }

        public long getHitCount() {
            return hitCount.get();
        }

        public long getMissCount() {
            return missCount.get();
        }
    }

    /**
     * Logging decorator for cache component
     */
    public static class LoggingDecorator implements CacheComponent {
        private final CacheComponent delegate;

        public LoggingDecorator(CacheComponent delegate) {
            this.delegate = delegate;
        }

        @Override
        public FinalShortlistDTO get(String jobId) {
            logger.debug("Getting cache for job: {}", jobId);
            FinalShortlistDTO result = delegate.get(jobId);
            if (result != null) {
                logger.debug("Cache hit for job: {}", jobId);
            } else {
                logger.debug("Cache miss for job: {}", jobId);
            }
            return result;
        }

        @Override
        public void put(String jobId, FinalShortlistDTO shortlist, long ttl) {
            logger.debug("Putting cache for job: {}", jobId);
            delegate.put(jobId, shortlist, ttl);
        }

        @Override
        public void evict(String jobId) {
            logger.debug("Evicting cache for job: {}", jobId);
            delegate.evict(jobId);
        }

        @Override
        public void clear() {
            logger.warn("Clearing all cache");
            delegate.clear();
        }

        @Override
        public boolean containsKey(String jobId) {
            return delegate.containsKey(jobId);
        }
    }

    // The decorated cache component
    private final CacheComponent cacheComponent;

    public ShortlistCacheService() {
        // Create the base component
        CacheComponent baseComponent = new RedisCache(redisTemplate);
        
        // Apply decorators
        CacheComponent statsComponent = new StatisticsDecorator(baseComponent);
        this.cacheComponent = new LoggingDecorator(statsComponent);
    }
    
    /**
     * Gets a cached shortlist for a job
     * @param jobId The job ID
     * @return Cached shortlist or null if not found
     */
    public FinalShortlistDTO getCachedShortlist(String jobId) {
        if (!cacheEnabled) {
            return null;
        }
        
        FinalShortlistDTO result = cacheComponent.get(jobId);
        
        // Update access time
        if (result != null) {
            lastAccessTimes.put(jobId, LocalDateTime.now());
        }
        
        return result;
    }
    
    /**
     * Caches a shortlist for a job
     * @param jobId The job ID
     * @param shortlist The shortlist to cache
     */
    public void cacheShortlist(String jobId, FinalShortlistDTO shortlist) {
        if (!cacheEnabled) {
            return;
        }
        
        cacheComponent.put(jobId, shortlist, cacheTtl);
        lastAccessTimes.put(jobId, LocalDateTime.now());
    }
    
    /**
     * Clears cache for a specific job
     * @param jobId The job ID
     */
    public void clearJobCache(String jobId) {
        cacheComponent.evict(jobId);
        lastAccessTimes.remove(jobId);
    }
    
    /**
     * Clears all cache
     */
    public void clearAllCache() {
        cacheComponent.clear();
        lastAccessTimes.clear();
    }
    
    /**
     * Checks if a job has cached shortlist
     * @param jobId The job ID
     * @return True if cached, false otherwise
     */
    public boolean isCached(String jobId) {
        return cacheEnabled && cacheComponent.containsKey(jobId);
    }
    
    /**
     * Gets cache statistics
     * @return Map with cache statistics
     */
    public Map<String, Object> getCacheStats() {
        Map<String, Object> stats = new ConcurrentHashMap<>();
        
        if (cacheEnabled) {
            stats.put("enabled", true);
            stats.put("hitCount", hitCount.get());
            stats.put("missCount", missCount.get());
            stats.put("totalRequests", hitCount.get() + missCount.get());
            
            double hitRate = hitCount.get() + missCount.get() > 0 ? 
                    (double) hitCount.get() / (hitCount.get() + missCount.get()) * 100 : 0;
            stats.put("hitRate", String.format("%.2f%%", hitRate));
            
            stats.put("cacheSize", lastAccessTimes.size());
            stats.put("cacheTtl", cacheTtl);
            
            // Add last access times for frequently accessed items
            Map<String, Object> accessInfo = new ConcurrentHashMap<>();
            lastAccessTimes.entrySet().stream()
                    .limit(10) // Top 10 most recently accessed
                    .forEach(entry -> {
                        accessInfo.put(entry.getKey(), entry.getValue());
                    });
            stats.put("recentAccess", accessInfo);
        } else {
            stats.put("enabled", false);
        }
        
        return stats;
    }
    
    /**
     * Gets the current cache size
     * @return Number of cached items
     */
    public int getCacheSize() {
        return lastAccessTimes.size();
    }
    
    /**
     * Gets the cache hit count
     * @return Number of cache hits
     */
    public long getHitCount() {
        return hitCount.get();
    }
    
    /**
     * Gets the cache miss count
     * @return Number of cache misses
     */
    public long getMissCount() {
        return missCount.get();
    }
    
    /**
     * Gets the cache TTL
     * @return TTL in seconds
     */
    public long getCacheTtl() {
        return cacheTtl;
    }
    
    /**
     * Sets the cache TTL
     * @param ttl TTL in seconds
     */
    public void setCacheTtl(long ttl) {
        this.cacheTtl = ttl;
    }
    
    /**
     * Checks if cache is enabled
     * @return True if enabled, false otherwise
     */
    public boolean isCacheEnabled() {
        return cacheEnabled;
    }
    
    /**
     * Enables or disables the cache
     * @param enabled True to enable, false to disable
     */
    public void setCacheEnabled(boolean enabled) {
        this.cacheEnabled = enabled;
    }
}