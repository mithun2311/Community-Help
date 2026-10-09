package com.community.backend.dto;
import java.time.LocalDateTime;
import com.community.backend.entity.Report;
import com.community.backend.entity.ReportStatus;
import com.community.backend.entity.ReportType;
public class ReportResponse {
    private final Long id;
    private final Long reportedById;
    private final Long reportedUserId;
    private final Long helpRequestId;
    private final Long chatMessageId;
    private final ReportType type;
    private final ReportStatus status;
    private final String reason;
    private final LocalDateTime createdAt;
    public ReportResponse(Long id,Long reportedById,Long reportedUserId,Long helpRequestId,Long chatMessageId,ReportType type,ReportStatus status,String reason,LocalDateTime createdAt) {
        this.id=id; this.reportedById=reportedById; this.reportedUserId=reportedUserId; this.helpRequestId=helpRequestId; this.chatMessageId=chatMessageId; this.type=type; this.status=status; this.reason=reason; this.createdAt=createdAt;
    }
    public static ReportResponse from(Report report) { return new ReportResponse(report.getId(),report.getReportedBy()==null?null:report.getReportedBy().getId(),report.getReportedUser()==null?null:report.getReportedUser().getId(),report.getHelpRequest()==null?null:report.getHelpRequest().getId(),report.getChatMessage()==null?null:report.getChatMessage().getId(),report.getType(),report.getStatus(),report.getReason(),report.getCreatedAt()); }
    public Long getId() { return id; }
    public Long getReportedById() { return reportedById; }
    public Long getReportedUserId() { return reportedUserId; }
    public Long getHelpRequestId() { return helpRequestId; }
    public Long getChatMessageId() { return chatMessageId; }
    public ReportType getType() { return type; }
    public ReportStatus getStatus() { return status; }
    public String getReason() { return reason; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
