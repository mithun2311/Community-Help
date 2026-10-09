package com.community.backend.dto;
import java.time.LocalDateTime;
import com.community.backend.entity.ReportStatus;
import com.community.backend.entity.ReportType;
public class AdminReportResponse {
    private final Long id;
    private final Long reporterId;
    private final String reporterEmail;
    private final Long reportedUserId;
    private final String reportedUserEmail;
    private final Long helpRequestId;
    private final Long chatMessageId;
    private final ReportType type;
    private final ReportStatus status;
    private final String reason;
    private final LocalDateTime createdAt;
    public AdminReportResponse(Long id,Long reporterId,String reporterEmail,Long reportedUserId,String reportedUserEmail,Long helpRequestId,Long chatMessageId,ReportType type,ReportStatus status,String reason,LocalDateTime createdAt) {
        this.id=id;
        this.reporterId=reporterId;
        this.reporterEmail=reporterEmail;
        this.reportedUserId=reportedUserId;
        this.reportedUserEmail=reportedUserEmail;
        this.helpRequestId=helpRequestId;
        this.chatMessageId=chatMessageId;
        this.type=type;
        this.status=status;
        this.reason=reason;
        this.createdAt=createdAt;
    }
    public Long getId() { return id; }
    public Long getReporterId() { return reporterId; }
    public String getReporterEmail() { return reporterEmail; }
    public Long getReportedUserId() { return reportedUserId; }
    public String getReportedUserEmail() { return reportedUserEmail; }
    public Long getHelpRequestId() { return helpRequestId; }
    public Long getChatMessageId() { return chatMessageId; }
    public ReportType getType() { return type; }
    public ReportStatus getStatus() { return status; }
    public String getReason() { return reason; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
