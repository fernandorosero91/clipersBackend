package com.clipers.clipers.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class VideoProcessingResponse {
    @JsonProperty("transcripcion")
    private String transcription;

    @JsonProperty("perfil")
    private Profile profile;

    public VideoProcessingResponse() {}

    public VideoProcessingResponse(String transcription, Profile profile) {
        this.transcription = transcription;
        this.profile = profile;
    }

    public String getTranscription() {
        return transcription;
    }

    public void setTranscription(String transcription) {
        this.transcription = transcription;
    }

    public Profile getProfile() {
        return profile;
    }

    public void setProfile(Profile profile) {
        this.profile = profile;
    }

    public static class Profile {
        @JsonProperty("nombre")
        private String name;

        @JsonProperty("profesion")
        private String profession;

        @JsonProperty("experiencia")
        private String experience;

        @JsonProperty("educacion")
        private String education;

        @JsonProperty("tecnologias")
        private String technologies;

        @JsonProperty("idiomas")
        private String languages;

        @JsonProperty("logros")
        private String achievements;

        @JsonProperty("habilidades_blandas")
        private String softSkills;

        public Profile() {}

        public Profile(String name, String profession, String experience, String education,
                      String technologies, String languages, String achievements, String softSkills) {
            this.name = name;
            this.profession = profession;
            this.experience = experience;
            this.education = education;
            this.technologies = technologies;
            this.languages = languages;
            this.achievements = achievements;
            this.softSkills = softSkills;
        }

        // Getters and setters
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getProfession() { return profession; }
        public void setProfession(String profession) { this.profession = profession; }

        public String getExperience() { return experience; }
        public void setExperience(String experience) { this.experience = experience; }

        public String getEducation() { return education; }
        public void setEducation(String education) { this.education = education; }

        public String getTechnologies() { return technologies; }
        public void setTechnologies(String technologies) { this.technologies = technologies; }

        public String getLanguages() { return languages; }
        public void setLanguages(String languages) { this.languages = languages; }

        public String getAchievements() { return achievements; }
        public void setAchievements(String achievements) { this.achievements = achievements; }

        public String getSoftSkills() { return softSkills; }
        public void setSoftSkills(String softSkills) { this.softSkills = softSkills; }
    }
}