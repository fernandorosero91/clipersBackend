package com.clipers.clipers.dto.matching;package com.clipers.clipers.dto.matching;



import java.util.List;import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**

 * DTO para transferir información del candidato a MicroSelectIA API/**

 * Coincide exactamente con el schema CandidateSchema de Python (Pydantic) * DTO para transferir información del candidato a la API de IA MicroSelectIA

 *  * Debe coincidir exactamente con el schema CandidateSchema de Python (Pydantic)

 * Se construye a partir de la entidad User + ATSProfile * 

 *  * Este DTO se construye a partir de la entidad User + ATSProfile

 * IMPORTANTE: Esta es la API de MATCHING (MicroSelectIA) */

 * NO confundir con la API de extracción de videos de Clipperspublic class CandidateDTO {

 */    

public class CandidateDTO {    private String id;

        private String name;

    private String id;    private String email;

    private String name;    private String summary;

    private String email;    

    private String summary;    private List<EducationDTO> education;

        private List<ExperienceDTO> experience;

    private List<EducationDTO> education;    private List<String> skills;

    private List<ExperienceDTO> experience;    private List<String> languages;

    private List<String> skills;    

    private List<String> languages;    // Constructors

        public CandidateDTO() {}

    // Constructors    

    public CandidateDTO() {}    public CandidateDTO(String id, String name, String email) {

            this.id = id;

    public CandidateDTO(String id, String name, String email) {        this.name = name;

        this.id = id;        this.email = email;

        this.name = name;    }

        this.email = email;    

    }    // Getters and Setters

        public String getId() {

    // Getters and Setters        return id;

    public String getId() {    }

        return id;    

    }    public void setId(String id) {

            this.id = id;

    public void setId(String id) {    }

        this.id = id;    

    }    public String getName() {

            return name;

    public String getName() {    }

        return name;    

    }    public void setName(String name) {

            this.name = name;

    public void setName(String name) {    }

        this.name = name;    

    }    public String getEmail() {

            return email;

    public String getEmail() {    }

        return email;    

    }    public void setEmail(String email) {

            this.email = email;

    public void setEmail(String email) {    }

        this.email = email;    

    }    public String getSummary() {

            return summary;

    public String getSummary() {    }

        return summary;    

    }    public void setSummary(String summary) {

            this.summary = summary;

    public void setSummary(String summary) {    }

        this.summary = summary;    

    }    public List<EducationDTO> getEducation() {

            return education;

    public List<EducationDTO> getEducation() {    }

        return education;    

    }    public void setEducation(List<EducationDTO> education) {

            this.education = education;

    public void setEducation(List<EducationDTO> education) {    }

        this.education = education;    

    }    public List<ExperienceDTO> getExperience() {

            return experience;

    public List<ExperienceDTO> getExperience() {    }

        return experience;    

    }    public void setExperience(List<ExperienceDTO> experience) {

            this.experience = experience;

    public void setExperience(List<ExperienceDTO> experience) {    }

        this.experience = experience;    

    }    public List<String> getSkills() {

            return skills;

    public List<String> getSkills() {    }

        return skills;    

    }    public void setSkills(List<String> skills) {

            this.skills = skills;

    public void setSkills(List<String> skills) {    }

        this.skills = skills;    

    }    public List<String> getLanguages() {

            return languages;

    public List<String> getLanguages() {    }

        return languages;    

    }    public void setLanguages(List<String> languages) {

            this.languages = languages;

    public void setLanguages(List<String> languages) {    }

        this.languages = languages;}

    }
}
