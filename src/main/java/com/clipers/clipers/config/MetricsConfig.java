package com.clipers.clipers.config;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import io.micrometer.prometheus.PrometheusMeterRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for metrics and monitoring
 */
@Configuration
public class MetricsConfig {

    /**
     * Creates a timer for measuring shortlist generation time
     * @param registry MeterRegistry
     * @return Timer
     */
    @Bean
    public Timer shortlistGenerationTimer(MeterRegistry registry) {
        return Timer.builder("shortlist.generation.time")
                .description("Time taken to generate candidate shortlist")
                .tag("component", "ai-integration")
                .register(registry);
    }

    /**
     * Creates a counter for tracking cache hits
     * @param registry MeterRegistry
     * @return Counter
     */
    @Bean
    public io.micrometer.core.instrument.Counter cacheHitsCounter(MeterRegistry registry) {
        return io.micrometer.core.instrument.Counter.builder("cache.hits")
                .description("Number of cache hits")
                .tag("component", "redis-cache")
                .register(registry);
    }

    /**
     * Creates a counter for tracking cache misses
     * @param registry MeterRegistry
     * @return Counter
     */
    @Bean
    public io.micrometer.core.instrument.Counter cacheMissesCounter(MeterRegistry registry) {
        return io.micrometer.core.instrument.Counter.builder("cache.misses")
                .description("Number of cache misses")
                .tag("component", "redis-cache")
                .register(registry);
    }
}