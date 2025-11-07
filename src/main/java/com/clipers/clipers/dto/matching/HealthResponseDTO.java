package com.clipers.clipers.dto.matching;

public class HealthResponseDTO {
    private String status;
    private Boolean modelLoaded;
    private Integer coldStartSeconds;

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Boolean getModelLoaded() { return modelLoaded; }
    public void setModelLoaded(Boolean modelLoaded) { this.modelLoaded = modelLoaded; }
    public Integer getColdStartSeconds() { return coldStartSeconds; }
    public void setColdStartSeconds(Integer coldStartSeconds) { this.coldStartSeconds = coldStartSeconds; }
}