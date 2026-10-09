package com.community.backend.dto;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
public class HelperProfileRequest {
    @NotBlank(message="At least one skill or category is required")
    @Size(max=500,message="Skills cannot exceed 500 characters")
    private String skills;
    @NotNull(message="Availability is required")
    private Boolean available;
    @NotNull(message="Maximum radius is required")
    @DecimalMin(value="1.0",message="Maximum radius must be at least 1 km")
    @DecimalMax(value="100.0",message="Maximum radius cannot exceed 100 km")
    private Double maxRadiusKm;
    public String getSkills() { return skills; }
    public Boolean getAvailable() { return available; }
    public Double getMaxRadiusKm() { return maxRadiusKm; }
    public void setSkills(String skills) { this.skills=skills; }
    public void setAvailable(Boolean available) { this.available=available; }
    public void setMaxRadiusKm(Double maxRadiusKm) { this.maxRadiusKm=maxRadiusKm; }
}
