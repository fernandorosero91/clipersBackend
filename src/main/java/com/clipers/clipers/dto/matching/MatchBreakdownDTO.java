package com.clipers.clipers.dto.matching;

import java.util.List;

/**
 * DTO de desglose detallado del match desde MicroSelectIA API
 * Coincide con MatchBreakdown de Python (Pydantic)
 * 
 * Contiene scores individuales y explicaciones del matching
 */
public class MatchBreakdownDTO {
    
    private Double skillsScore;
    private Double experienceScore;
    private Double educationScore;
    private Double semanticScore;
    
    private List<String> matchedSkills;
    private List<String> missingSkills;
    
    private String explanation;
    private String recommendations;
    
    // Constructors
    public MatchBreakdownDTO() {}
    
    // Getters and Setters
    public Double getSkillsScore() {
        return skillsScore;
    }
    
    public void setSkillsScore(Double skillsScore) {
        this.skillsScore = skillsScore;
    }
    
    public Double getExperienceScore() {
        return experienceScore;
    }
    
    public void setExperienceScore(Double experienceScore) {
        this.experienceScore = experienceScore;
    }
    
    public Double getEducationScore() {
        return educationScore;
    }
    
    public void setEducationScore(Double educationScore) {
        this.educationScore = educationScore;
    }
    
    public Double getSemanticScore() {
        return semanticScore;
    }
    
    public void setSemanticScore(Double semanticScore) {
        this.semanticScore = semanticScore;
    }
    
    public List<String> getMatchedSkills() {
        return matchedSkills;
    }
    
    public void setMatchedSkills(List<String> matchedSkills) {
        this.matchedSkills = matchedSkills;
    }
    
    public List<String> getMissingSkills() {
        return missingSkills;
    }
    
    public void setMissingSkills(List<String> missingSkills) {
        this.missingSkills = missingSkills;
    }
    
    public String getExplanation() {
        return explanation;
    }
    
    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }
    
    public String getRecommendations() {
        return recommendations;
    }
    
    public void setRecommendations(String recommendations) {
        this.recommendations = recommendations;
    }
}
