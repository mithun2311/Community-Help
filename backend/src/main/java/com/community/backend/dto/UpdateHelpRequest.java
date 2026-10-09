package com.community.backend.dto;
import com.community.backend.entity.HelpUrgency;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
public class UpdateHelpRequest {
    @NotBlank(message="Title is required")
    @Size(max=120,message="Title cannot exceed 120 characters")
    private String title;
    @NotBlank(message="Description is required")
    @Size(max=2000,message="Description cannot exceed 2000 characters")
    private String description;
    @NotNull(message="Urgency is required")
    private HelpUrgency urgency;
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public HelpUrgency getUrgency() { return urgency; }
    public void setTitle(String title) { this.title=title; }
    public void setDescription(String description) { this.description=description; }
    public void setUrgency(HelpUrgency urgency) { this.urgency=urgency; }
}
