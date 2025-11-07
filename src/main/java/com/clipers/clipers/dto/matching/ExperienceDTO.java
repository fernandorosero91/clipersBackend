package com.clipers.clipers.dto.matching;

public class ExperienceDTO {
    private String company;
    private String role;
    private Integer startYear;
    private Integer endYear;
    private Integer years;

    public String getCompany() { return company; }
    public void setCompany(String company) { this.company = company; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public Integer getStartYear() { return startYear; }
    public void setStartYear(Integer startYear) { this.startYear = startYear; }
    public Integer getEndYear() { return endYear; }
    public void setEndYear(Integer endYear) { this.endYear = endYear; }
    public Integer getYears() { return years; }
    public void setYears(Integer years) { this.years = years; }
}