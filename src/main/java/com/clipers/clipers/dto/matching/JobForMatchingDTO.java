package com.clipers.clipers.dto.matching;package com.clipers.clipers.dto.matching;



import java.util.List;import java.util.List;



/**/**

 * DTO para transferir información del Job a MicroSelectIA API * DTO para transferir información del Job a la API de IA MicroSelectIA

 * Coincide con el schema JobSchema de Python (Pydantic) * Debe coincidir con el schema JobSchema de Python (Pydantic)

 *  */

 * IMPORTANTE: Esta es la API de MATCHING (MicroSelectIA)public class JobForMatchingDTO {

 * NO confundir con la API de extracción de videos de Clippers    

 */    private String id;

public class JobForMatchingDTO {    private String title;

        private String description;

    private String id;    private List<String> requirements;

    private String title;    private List<String> skills;

    private String description;    private String location;

    private List<String> requirements;    private String type;

    private List<String> skills;    

    private String location;    @SuppressWarnings("unused")

    private String type;    private Integer salaryMin;

    private Integer salaryMin;    

    private Integer salaryMax;    @SuppressWarnings("unused")

        private Integer salaryMax;

    // Constructors    

    public JobForMatchingDTO() {}    // Constructors

        public JobForMatchingDTO() {}

    public JobForMatchingDTO(String id, String title, String description) {    

        this.id = id;    public JobForMatchingDTO(String id, String title, String description) {

        this.title = title;        this.id = id;

        this.description = description;        this.title = title;

    }        this.description = description;

        }

    // Getters and Setters    

    public String getId() {    // Getters and Setters

        return id;    public String getId() {

    }        return id;

        }

    public void setId(String id) {    

        this.id = id;    public void setId(String id) {

    }        this.id = id;

        }

    public String getTitle() {    

        return title;    public String getTitle() {

    }        return title;

        }

    public void setTitle(String title) {    

        this.title = title;    public void setTitle(String title) {

    }        this.title = title;

        }

    public String getDescription() {    

        return description;    public String getDescription() {

    }        return description;

        }

    public void setDescription(String description) {    

        this.description = description;    public void setDescription(String description) {

    }        this.description = description;

        }

    public List<String> getRequirements() {    

        return requirements;    public List<String> getRequirements() {

    }        return requirements;

        }

    public void setRequirements(List<String> requirements) {    

        this.requirements = requirements;    public void setRequirements(List<String> requirements) {

    }        this.requirements = requirements;

        }

    public List<String> getSkills() {    

        return skills;    public List<String> getSkills() {

    }        return skills;

        }

    public void setSkills(List<String> skills) {    

        this.skills = skills;    public void setSkills(List<String> skills) {

    }        this.skills = skills;

        }

    public String getLocation() {    

        return location;    public String getLocation() {

    }        return location;

        }

    public void setLocation(String location) {    

        this.location = location;    public void setLocation(String location) {

    }        this.location = location;

        }

    public String getType() {    

        return type;    public String getType() {

    }        return type;

        }

    public void setType(String type) {    

        this.type = type;    public void setType(String type) {

    }        this.type = type;

        }

    public Integer getSalaryMin() {    

        return salaryMin;    public Integer getSalaryMin() {

    }        return salaryMin;

        }

    public void setSalaryMin(Integer salaryMin) {    

        this.salaryMin = salaryMin;    public void setSalaryMin(Integer salaryMin) {

    }        this.salaryMin = salaryMin;

        }

    public Integer getSalaryMax() {    

        return salaryMax;    public Integer getSalaryMax() {

    }        return salaryMax;

        }

    public void setSalaryMax(Integer salaryMax) {    

        this.salaryMax = salaryMax;    public void setSalaryMax(Integer salaryMax) {

    }        this.salaryMax = salaryMax;

}    }

}
