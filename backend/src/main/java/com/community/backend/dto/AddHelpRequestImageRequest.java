package com.community.backend.dto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
public class AddHelpRequestImageRequest {
    @NotBlank(message="Image URL is required")
    @Size(max=2048,message="Image URL cannot exceed 2048 characters")
    private String imageUrl;
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl=imageUrl; }
}
