package com.community.backend.dto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
public class VerificationRequest {
    @NotBlank(message="Verification note is required")
    @Size(min=10,max=500,message="Verification note must contain between 10 and 500 characters")
    private String note;
    public String getNote() { return note; }
    public void setNote(String note) { this.note=note; }
}
