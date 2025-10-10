package com.clipers.ai.integration.service;

import java.util.*;
import com.clipers.clipers.entity.ATSProfile;
import com.clipers.clipers.entity.Education;
import com.clipers.clipers.entity.Experience;
import com.clipers.clipers.entity.Skill;
import com.clipers.clipers.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service for evaluating ATS profiles using different strategies
 * Implements Strategy pattern for flexible evaluation approaches
 */
@Service
public class ATSProfileEvaluator {

    private static final Logger logger = LoggerFactory.getLogger(ATSProfileEvaluator.class);

    /**
     * Strategy interface for profile evaluation
     */
    public interface EvaluationStrategy {
        double evaluate(ATSProfile profile, Map<String, Object> context);
    }

    /**
     * Strict evaluation strategy - high requirements for good scores
     */
    public static class StrictEvaluator implements EvaluationStrategy {
        @Override
        public double evaluate(ATSProfile profile, Map<String, Object> context) {
            double score = 0.0;
            
            // Skills evaluation (40% weight)
            score += evaluateSkillsStrict(profile) * 0.4;
            
            // Experience evaluation (40% weight)
            score += evaluateExperienceStrict(profile) * 0.4;
            
            // Education evaluation (20% weight)
            score += evaluateEducationStrict(profile) * 0.2;
            
            return Math.min(score, 1.0); // Cap at 1.0
        }
        
        private double evaluateSkillsStrict(ATSProfile profile) {
            if (profile.getSkills() == null || profile.getSkills().isEmpty()) {
                return 0.0; // Minimum score for no skills
            }
            
            // Count skills by level
            long expertSkills = profile.getSkills().stream()
                    .filter(skill -> skill.getLevel() == Skill.SkillLevel.EXPERT)
                    .count();
            
            long advancedSkills = profile.getSkills().stream()
                    .filter(skill -> skill.getLevel() == Skill.SkillLevel.ADVANCED)
                    .count();
            
            long intermediateSkills = profile.getSkills().stream()
                    .filter(skill -> skill.getLevel() == Skill.SkillLevel.INTERMEDIATE)
                    .count();
            
            // Calculate weighted score
            double score = (expertSkills * 1.0 + advancedSkills * 0.7 + intermediateSkills * 0.4) / profile.getSkills().size();
            
            return Math.max(score, 0.0); // Minimum score
        }
        
        private double evaluateExperienceStrict(ATSProfile profile) {
            if (profile.getExperience() == null || profile.getExperience().isEmpty()) {
                return 0.0; // Minimum score for no experience
            }
            
            // Calculate total years of experience
            double totalYears = profile.getExperience().stream()
                    .mapToDouble(this::getYearsFromExperienceStrict)
                    .sum();
            
            // Score based on total years of experience
            if (totalYears >= 10) return 1.0;
            if (totalYears >= 7) return 0.8;
            if (totalYears >= 5) return 0.6;
            if (totalYears >= 3) return 0.4;
            if (totalYears >= 1) return 0.2;
            return 0.0;
        }
        
        private double evaluateEducationStrict(ATSProfile profile) {
            if (profile.getEducation() == null || profile.getEducation().isEmpty()) {
                return 0.0; // Minimum score for no education
            }
            
            // Check for degrees
            boolean hasBachelor = profile.getEducation().stream()
                    .anyMatch(edu -> "Bachelor".equalsIgnoreCase(edu.getDegree()));
            
            boolean hasMaster = profile.getEducation().stream()
                    .anyMatch(edu -> "Master".equalsIgnoreCase(edu.getDegree()));
            
            boolean hasPhD = profile.getEducation().stream()
                    .anyMatch(edu -> "PhD".equalsIgnoreCase(edu.getDegree()) || 
                                    "Doctorate".equalsIgnoreCase(edu.getDegree()));
            
            if (hasPhD) return 1.0;
            if (hasMaster) return 0.8;
            if (hasBachelor) return 0.6;
            return 0.0;
        }
        
        private double getYearsFromExperienceStrict(Experience experience) {
            // More detailed estimation
            String description = experience.getDescription().toLowerCase();
            String position = experience.getPosition().toLowerCase();
            
            if (description.contains("director") || description.contains("vp") || 
                position.contains("director") || position.contains("vp")) {
                return 10.0;
            } else if (description.contains("senior") || description.contains("lead") || 
                       position.contains("senior") || position.contains("lead")) {
                return 7.0;
            } else if (description.contains("mid") || description.contains("level") || 
                       position.contains("mid") || position.contains("level")) {
                return 4.0;
            } else {
                return 2.0;
            }
        }
    }

    /**
     * Balanced evaluation strategy - moderate requirements
     */
    public static class BalancedEvaluator implements EvaluationStrategy {
        @Override
        public double evaluate(ATSProfile profile, Map<String, Object> context) {
            double score = 0.0;
            
            // Skills evaluation (35% weight)
            score += evaluateSkillsBalanced(profile) * 0.35;
            
            // Experience evaluation (35% weight)
            score += evaluateExperienceBalanced(profile) * 0.35;
            
            // Education evaluation (20% weight)
            score += evaluateEducationBalanced(profile) * 0.2;
            
            // Profile completeness (10% weight)
            score += evaluateCompletenessBalanced(profile) * 0.1;
            
            return Math.min(score, 1.0); // Cap at 1.0
        }
        
        private double evaluateSkillsBalanced(ATSProfile profile) {
            if (profile.getSkills() == null || profile.getSkills().isEmpty()) {
                return 0.3; // Minimum score for no skills
            }
            
            // Count skills by level
            long expertSkills = profile.getSkills().stream()
                    .filter(skill -> skill.getLevel() == Skill.SkillLevel.EXPERT)
                    .count();
            
            long advancedSkills = profile.getSkills().stream()
                    .filter(skill -> skill.getLevel() == Skill.SkillLevel.ADVANCED)
                    .count();
            
            long intermediateSkills = profile.getSkills().stream()
                    .filter(skill -> skill.getLevel() == Skill.SkillLevel.INTERMEDIATE)
                    .count();
            
            // Calculate weighted score
            double score = (expertSkills * 1.0 + advancedSkills * 0.8 + intermediateSkills * 0.5) / profile.getSkills().size();
            
            return Math.max(score, 0.3); // Minimum score
        }
        
        private double evaluateExperienceBalanced(ATSProfile profile) {
            if (profile.getExperience() == null || profile.getExperience().isEmpty()) {
                return 0.3; // Minimum score for no experience
            }
            
            // Calculate total years of experience
            double totalYears = profile.getExperience().stream()
                    .mapToDouble(this::getYearsFromExperienceBalanced)
                    .sum();
            
            // Score based on total years of experience
            if (totalYears >= 10) return 1.0;
            if (totalYears >= 5) return 0.8;
            if (totalYears >= 2) return 0.6;
            if (totalYears >= 1) return 0.4;
            return 0.3;
        }
        
        private double evaluateEducationBalanced(ATSProfile profile) {
            if (profile.getEducation() == null || profile.getEducation().isEmpty()) {
                return 0.5; // Default score for no education
            }
            
            // Check for degrees
            boolean hasBachelor = profile.getEducation().stream()
                    .anyMatch(edu -> "Bachelor".equalsIgnoreCase(edu.getDegree()));
            
            boolean hasMaster = profile.getEducation().stream()
                    .anyMatch(edu -> "Master".equalsIgnoreCase(edu.getDegree()));
            
            boolean hasPhD = profile.getEducation().stream()
                    .anyMatch(edu -> "PhD".equalsIgnoreCase(edu.getDegree()) || 
                                    "Doctorate".equalsIgnoreCase(edu.getDegree()));
            
            if (hasPhD) return 1.0;
            if (hasMaster) return 0.8;
            if (hasBachelor) return 0.6;
            return 0.5;
        }
        
        private double evaluateCompletenessBalanced(ATSProfile profile) {
            boolean hasSummary = profile.getSummary() != null && !profile.getSummary().trim().isEmpty();
            boolean hasSkills = !profile.getSkills().isEmpty();
            boolean hasExperience = !profile.getExperience().isEmpty();
            boolean hasEducation = !profile.getEducation().isEmpty();
            
            int completeSections = 0;
            if (hasSummary) completeSections++;
            if (hasSkills) completeSections++;
            if (hasExperience) completeSections++;
            if (hasEducation) completeSections++;
            
            return (double) completeSections / 4.0;
        }
        
        private double getYearsFromExperienceBalanced(Experience experience) {
            // More detailed estimation
            String description = experience.getDescription().toLowerCase();
            String position = experience.getPosition().toLowerCase();
            
            if (description.contains("director") || description.contains("vp") || 
                position.contains("director") || position.contains("vp")) {
                return 8.0;
            } else if (description.contains("senior") || description.contains("lead") || 
                       position.contains("senior") || position.contains("lead")) {
                return 5.0;
            } else if (description.contains("mid") || description.contains("level") || 
                       position.contains("mid") || position.contains("level")) {
                return 3.0;
            } else {
                return 1.5;
            }
        }
    }

    /**
     * Lenient evaluation strategy - lower requirements for good scores
     */
    public static class LenientEvaluator implements EvaluationStrategy {
        @Override
        public double evaluate(ATSProfile profile, Map<String, Object> context) {
            double score = 0.0;
            
            // Skills evaluation (30% weight)
            score += evaluateSkillsLenient(profile) * 0.3;
            
            // Experience evaluation (30% weight)
            score += evaluateExperienceLenient(profile) * 0.3;
            
            // Education evaluation (20% weight)
            score += evaluateEducationLenient(profile) * 0.2;
            
            // Profile completeness (20% weight)
            score += evaluateCompletenessLenient(profile) * 0.2;
            
            return Math.min(score, 1.0); // Cap at 1.0
        }
        
        private double evaluateSkillsLenient(ATSProfile profile) {
            if (profile.getSkills() == null || profile.getSkills().isEmpty()) {
                return 0.5; // Higher minimum score for no skills
            }
            
            // Any skills get a good base score
            double baseScore = 0.6;
            
            // Bonus for having skills
            if (profile.getSkills().size() >= 5) {
                baseScore += 0.2;
            }
            
            return Math.min(baseScore, 1.0);
        }
        
        private double evaluateExperienceLenient(ATSProfile profile) {
            if (profile.getExperience() == null || profile.getExperience().isEmpty()) {
                return 0.6; // Higher minimum score for no experience
            }
            
            // Any experience gets a good base score
            double baseScore = 0.7;
            
            // Bonus for more experience
            if (profile.getExperience().size() >= 3) {
                baseScore += 0.2;
            }
            
            return Math.min(baseScore, 1.0);
        }
        
        private double evaluateEducationLenient(ATSProfile profile) {
            if (profile.getEducation() == null || profile.getEducation().isEmpty()) {
                return 0.7; // Higher minimum score for no education
            }
            
            // Any education gets a good base score
            return 0.8;
        }
        
        private double evaluateCompletenessLenient(ATSProfile profile) {
            boolean hasSummary = profile.getSummary() != null && !profile.getSummary().trim().isEmpty();
            boolean hasSkills = !profile.getSkills().isEmpty();
            boolean hasExperience = !profile.getExperience().isEmpty();
            boolean hasEducation = !profile.getEducation().isEmpty();
            
            int completeSections = 0;
            if (hasSummary) completeSections++;
            if (hasSkills) completeSections++;
            if (hasExperience) completeSections++;
            if (hasEducation) completeSections++;
            
            // Lenient scoring - fewer sections needed for good score
            if (completeSections >= 2) return 0.8;
            if (completeSections >= 1) return 0.5;
            return 0.3;
        }
    }

    // Default strategy
    private EvaluationStrategy defaultStrategy = new BalancedEvaluator();

    /**
     * Evaluates an ATS profile using the default strategy
     * @param user The user with ATS profile
     * @return ATS score (0.0 to 1.0)
     */
    public double evaluateATSProfile(User user) {
        return evaluateATSProfile(user, defaultStrategy);
    }

    /**
     * Evaluates an ATS profile using a specific strategy
     * @param user The user with ATS profile
     * @param strategy The evaluation strategy to use
     * @return ATS score (0.0 to 1.0)
     */
    public double evaluateATSProfile(User user, EvaluationStrategy strategy) {
        if (user == null || user.getAtsProfile() == null) {
            logger.warn("User or ATS profile is null for user: {}", user != null ? user.getId() : "null");
            return 0.0;
        }

        ATSProfile profile = user.getAtsProfile();
        logger.debug("Evaluating ATS profile for user: {}", user.getId());

        // Create context for evaluation
        Map<String, Object> context = new HashMap<>();
        context.put("evaluationDate", new Date());

        // Evaluate using the provided strategy
        double score = strategy.evaluate(profile, context);
        
        logger.debug("ATS evaluation score for user {}: {}", user.getId(), score);
        return score;
    }

    /**
     * Evaluates skills match against job requirements
     * @param profile The ATS profile
     * @param requiredSkills List of required skills
     * @return Skills match score (0.0 to 1.0)
     */
    public double evaluateSkillsMatch(ATSProfile profile, List<String> requiredSkills) {
        if (profile.getSkills() == null || profile.getSkills().isEmpty() || 
            requiredSkills == null || requiredSkills.isEmpty()) {
            return 0.0;
        }

        // Extract skill names from profile
        List<String> profileSkills = profile.getSkills().stream()
                .map(Skill::getName)
                .map(String::toLowerCase)
                .collect(Collectors.toList());

        // Count matches
        long matches = requiredSkills.stream()
                .map(String::toLowerCase)
                .filter(profileSkills::contains)
                .count();

        return (double) matches / requiredSkills.size();
    }

    /**
     * Sets the evaluation strategy to use
     * @param strategy The evaluation strategy
     */
    public void setEvaluationStrategy(EvaluationStrategy strategy) {
        this.defaultStrategy = strategy;
        logger.info("Evaluation strategy set to: {}", strategy.getClass().getSimpleName());
    }

    /**
     * Gets the current evaluation strategy
     * @return The current evaluation strategy
     */
    public EvaluationStrategy getEvaluationStrategy() {
        return defaultStrategy;
    }
}