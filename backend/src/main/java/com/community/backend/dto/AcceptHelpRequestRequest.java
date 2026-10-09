package com.community.backend.dto;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
public class AcceptHelpRequestRequest {
    @NotNull(message="Current latitude is required")
    @DecimalMin(value="-90.0")
    @DecimalMax(value="90.0")
    private Double latitude;
    @NotNull(message="Current longitude is required")
    @DecimalMin(value="-180.0")
    @DecimalMax(value="180.0")
    private Double longitude;
    public Double getLatitude() { return latitude; }
    public Double getLongitude() { return longitude; }
    public void setLatitude(Double latitude) { this.latitude=latitude; }
    public void setLongitude(Double longitude) { this.longitude=longitude; }
}
