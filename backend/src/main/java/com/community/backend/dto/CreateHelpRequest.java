package com.community.backend.dto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.DecimalMax;
import com.community.backend.entity.HelpCategory;
import com.community.backend.entity.HelpUrgency;
public class CreateHelpRequest {
    @NotBlank(message="Title is required")
    @Size(max=120,message="Title cannot exceed 120 characters")
    private String title;
    @NotBlank(message="Description is required")
    @Size(max=2000,message="Description cannot exceed 2000 characters")
    private String description;
    @NotNull(message="Category is required")
    private HelpCategory category;
    @NotNull(message="Urgency is required")
    private HelpUrgency urgency;
    @NotNull(message="Latitude is required")
    @DecimalMin(value="-90.0")
    @DecimalMax(value="90.0")
    private Double latitude;
    @NotNull(message="Longitude is required")
    @DecimalMin(value="-180.0")
    @DecimalMax(value="180.0")
    private Double longitude;
    public String getTitle() {
        return title;
    }
    public String getDescription() {
        return description;
    }
    public HelpCategory getCategory() {
        return category;
    }
    public HelpUrgency getUrgency() {
        return urgency;
    }
    public Double getLatitude() {
        return latitude;
    }
    public Double getLongitude() {
        return longitude;
    }
    public void setTitle(String title) {
        this.title=title;
    }
    public void setDescription(String description) {
        this.description=description;
    }
    public void setCategory(HelpCategory category) {
        this.category=category;
    }
    public void setUrgency(HelpUrgency urgency) {
        this.urgency=urgency;
    }
    public void setLatitude(Double latitude) {
        this.latitude=latitude;
    }
    public void setLongitude(Double longitude) {
        this.longitude=longitude;
    }
}
