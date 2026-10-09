package com.community.backend.dto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
public class ReportRequest {
    @NotBlank(message="Report reason is required")
    @Size(max=1000,message="Report reason cannot exceed 1000 characters")
    private String reason;
    public String getReason() {
        return reason;
    }
    public void setReason(String reason) {
        this.reason=reason;
    }
}
