package com.clipers.clipers.dto.matching;

public class MatchBreakdownDTO {
    private Double skillsMatch;
    private Double experienceMatch;
    private Double educationMatch;
    private Double semanticMatch;
    private Double locationMatch; // optional

    public Double getSkillsMatch() { return skillsMatch; }
    public void setSkillsMatch(Double skillsMatch) { this.skillsMatch = skillsMatch; }
    public Double getExperienceMatch() { return experienceMatch; }
    public void setExperienceMatch(Double experienceMatch) { this.experienceMatch = experienceMatch; }
    public Double getEducationMatch() { return educationMatch; }
    public void setEducationMatch(Double educationMatch) { this.educationMatch = educationMatch; }
    public Double getSemanticMatch() { return semanticMatch; }
    public void setSemanticMatch(Double semanticMatch) { this.semanticMatch = semanticMatch; }
    public Double getLocationMatch() { return locationMatch; }
    public void setLocationMatch(Double locationMatch) { this.locationMatch = locationMatch; }
}