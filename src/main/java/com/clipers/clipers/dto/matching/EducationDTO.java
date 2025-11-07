package com.clipers.clipers.dto.matching;package com.clipers.clipers.dto.matching;



import com.fasterxml.jackson.annotation.JsonProperty;import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**

 * DTO para transferir información de educación del candidato a MicroSelectIA API/**

 * Coincide con el schema EducationSchema de Python (Pydantic) * DTO para transferir información de educación del candidato a la API de IA

 *  * Debe coincidir con el schema EducationSchema de Python (Pydantic)

 * NO CONFUNDIR con la API de extracción de videos de Clippers */

 * Esta es exclusivamente para el matching de candidatos con IApublic class EducationDTO {

 */    

public class EducationDTO {    private String institution;

        private String degree;

    private String institution;    private String field;

    private String degree;    

    private String field;    @JsonProperty("start_date")

        private String startDate;

    @JsonProperty("start_date")    

    private String startDate;    @JsonProperty("end_date")

        private String endDate;

    @JsonProperty("end_date")    

    private String endDate;    // Constructors

        public EducationDTO() {}

    // Constructors    

    public EducationDTO() {}    public EducationDTO(String institution, String degree, String field, String startDate, String endDate) {

            this.institution = institution;

    public EducationDTO(String institution, String degree, String field, String startDate, String endDate) {        this.degree = degree;

        this.institution = institution;        this.field = field;

        this.degree = degree;        this.startDate = startDate;

        this.field = field;        this.endDate = endDate;

        this.startDate = startDate;    }

        this.endDate = endDate;    

    }    // Getters and Setters

        public String getInstitution() {

    // Getters and Setters        return institution;

    public String getInstitution() {    }

        return institution;    

    }    public void setInstitution(String institution) {

            this.institution = institution;

    public void setInstitution(String institution) {    }

        this.institution = institution;    

    }    public String getDegree() {

            return degree;

    public String getDegree() {    }

        return degree;    

    }    public void setDegree(String degree) {

            this.degree = degree;

    public void setDegree(String degree) {    }

        this.degree = degree;    

    }    public String getField() {

            return field;

    public String getField() {    }

        return field;    

    }    public void setField(String field) {

            this.field = field;

    public void setField(String field) {    }

        this.field = field;    

    }    public String getStartDate() {

            return startDate;

    public String getStartDate() {    }

        return startDate;    

    }    public void setStartDate(String startDate) {

            this.startDate = startDate;

    public void setStartDate(String startDate) {    }

        this.startDate = startDate;    

    }    public String getEndDate() {

            return endDate;

    public String getEndDate() {    }

        return endDate;    

    }    public void setEndDate(String endDate) {

            this.endDate = endDate;

    public void setEndDate(String endDate) {    }

        this.endDate = endDate;}

    }
}
