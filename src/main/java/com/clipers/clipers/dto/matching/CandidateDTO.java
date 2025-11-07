package com.clipers.clipers.dto.matching;

import java.util.List;

/**
 * DTO para transferir información del candidato a MicroSelectIA API
 * Coincide exactamente con el schema CandidateSchema de Python (Pydantic)
 * 
 * Se construye a partir de la entidad User + ATSProfile
 * 
 * IMPORTANTE: Esta es la API de MATCHING (MicroSelectIA)
 * NO confundir con la API de extracción de videos de Clippers
 */
public class CandidateDTO {
    
    // Campos básicos
    private String id;
    private String name;
    private String email;
    private String summary;
    private String location;
    
    // Experiencia y habilidades
    private List<String> skills;
    private Integer experienceYears;
    private List<ExperienceDTO> experience;
    
    // Educación e idiomas
    private List<EducationDTO> education;
    private List<String> languages;
    
    // Constructors
    public CandidateDTO() {}
    
    public CandidateDTO(String id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }
    
    // Getters and Setters
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getSummary() {
        return summary;
    }
    
    public void setSummary(String summary) {
        this.summary = summary;
    }
    
    public String getLocation() {
        return location;
    }
    
    public void setLocation(String location) {
        this.location = location;
    }
    
    public List<String> getSkills() {
        return skills;
    }
    
    public void setSkills(List<String> skills) {
        this.skills = skills;
    }
    
    public Integer getExperienceYears() {
        return experienceYears;
    }
    
    public void setExperienceYears(Integer experienceYears) {
        this.experienceYears = experienceYears;
    }
    
    public List<ExperienceDTO> getExperience() {
        return experience;
    }
    
    public void setExperience(List<ExperienceDTO> experience) {
        this.experience = experience;
    }
    
    public List<EducationDTO> getEducation() {
        return education;
    }
    
    public void setEducation(List<EducationDTO> education) {
        this.education = education;
    }
    
    public List<String> getLanguages() {
        return languages;
    }
    
    public void setLanguages(List<String> languages) {
        this.languages = languages;
    }
}
