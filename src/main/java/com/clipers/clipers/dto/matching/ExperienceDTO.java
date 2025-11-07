package com.clipers.clipers.dto.matching;package com.clipers.clipers.dto.matching;



import com.fasterxml.jackson.annotation.JsonProperty;import com.fasterxml.jackson.annotation.JsonProperty;



/**/**

 * DTO para transferir información de experiencia laboral del candidato a MicroSelectIA API * DTO para transferir información de experiencia laboral del candidato a la API de IA

 * Coincide con el schema ExperienceSchema de Python (Pydantic) * Debe coincidir con el schema ExperienceSchema de Python (Pydantic)

 *  */

 * NO CONFUNDIR con la API de extracción de videos de Clipperspublic class ExperienceDTO {

 * Esta es exclusivamente para el matching de candidatos con IA    

 */    private String company;

public class ExperienceDTO {    private String position;

        private String description;

    private String company;    

    private String position;    @JsonProperty("start_date")

    private String description;    private String startDate;

        

    @JsonProperty("start_date")    @JsonProperty("end_date")

    private String startDate;    private String endDate;

        

    @JsonProperty("end_date")    // Constructors

    private String endDate;    public ExperienceDTO() {}

        

    // Constructors    public ExperienceDTO(String company, String position, String description, String startDate, String endDate) {

    public ExperienceDTO() {}        this.company = company;

            this.position = position;

    public ExperienceDTO(String company, String position, String description, String startDate, String endDate) {        this.description = description;

        this.company = company;        this.startDate = startDate;

        this.position = position;        this.endDate = endDate;

        this.description = description;    }

        this.startDate = startDate;    

        this.endDate = endDate;    // Getters and Setters

    }    public String getCompany() {

            return company;

    // Getters and Setters    }

    public String getCompany() {    

        return company;    public void setCompany(String company) {

    }        this.company = company;

        }

    public void setCompany(String company) {    

        this.company = company;    public String getPosition() {

    }        return position;

        }

    public String getPosition() {    

        return position;    public void setPosition(String position) {

    }        this.position = position;

        }

    public void setPosition(String position) {    

        this.position = position;    public String getDescription() {

    }        return description;

        }

    public String getDescription() {    

        return description;    public void setDescription(String description) {

    }        this.description = description;

        }

    public void setDescription(String description) {    

        this.description = description;    public String getStartDate() {

    }        return startDate;

        }

    public String getStartDate() {    

        return startDate;    public void setStartDate(String startDate) {

    }        this.startDate = startDate;

        }

    public void setStartDate(String startDate) {    

        this.startDate = startDate;    public String getEndDate() {

    }        return endDate;

        }

    public String getEndDate() {    

        return endDate;    public void setEndDate(String endDate) {

    }        this.endDate = endDate;

        }

    public void setEndDate(String endDate) {}

        this.endDate = endDate;
    }
}
