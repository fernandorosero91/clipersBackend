package com.clipers.clipers.dto.matching;

import java.util.List;

public class CandidateDTO {
    private String id;
    private String name;
    private List<String> skills;
    private Integer experienceYears;
    private List<EducationDTO> education;
    private List<String> languages;
    private String summary;
    private String location;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public List<String> getSkills() { return skills; }
    public void setSkills(List<String> skills) { this.skills = skills; }
    public Integer getExperienceYears() { return experienceYears; }
    public void setExperienceYears(Integer experienceYears) { this.experienceYears = experienceYears; }
    public List<EducationDTO> getEducation() { return education; }
    public void setEducation(List<EducationDTO> education) { this.education = education; }
    public List<String> getLanguages() { return languages; }
    public void setLanguages(List<String> languages) { this.languages = languages; }
    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
}