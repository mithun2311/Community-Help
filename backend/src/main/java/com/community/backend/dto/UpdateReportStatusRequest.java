package com.community.backend.dto;
import com.community.backend.entity.ReportStatus;
import jakarta.validation.constraints.NotNull;
public class UpdateReportStatusRequest {
    @NotNull(message="Report status is required")
    private ReportStatus status;
    public ReportStatus getStatus() { return status; }
    public void setStatus(ReportStatus status) { this.status=status; }
}
