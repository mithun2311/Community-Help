package com.community.backend.entity;
import java.time.LocalDateTime;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
@Entity
@Table(name="reports")
public class Report {
    @Id
    @GeneratedValue
    private Long id;
    @ManyToOne
    @JoinColumn(name="reported_by")
    private User reportedBy;
    @ManyToOne
    @JoinColumn(name="reported_user")
    private User reportedUser;
    @ManyToOne
    @JoinColumn(name="help_request_id")
    private HelpRequest helpRequest;
    @ManyToOne
    @JoinColumn(name="chat_message_id")
    private ChatMessage chatMessage;
    @Enumerated(EnumType.STRING)
    private ReportType type;
    @Enumerated(EnumType.STRING)
    private ReportStatus status;
    private String reason;
    private LocalDateTime createdAt;
    public Long getId() {
        return id;
    }
    public User getReportedBy() {
        return reportedBy;
    }
    public User getReportedUser() {
        return reportedUser;
    }
    public HelpRequest getHelpRequest() {
        return helpRequest;
    }
    public ChatMessage getChatMessage() {
        return chatMessage;
    }
    public ReportType getType() {
        return type;
    }
    public ReportStatus getStatus() {
        return status;
    }
    public String getReason() {
        return reason;
    }
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    public void setReportedBy(User reportedBy) {
        this.reportedBy=reportedBy;
    }
    public void setReportedUser(User reportedUser) {
        this.reportedUser=reportedUser;
    }
    public void setHelpRequest(HelpRequest helpRequest) {
        this.helpRequest=helpRequest;
    }
    public void setChatMessage(ChatMessage chatMessage) {
        this.chatMessage=chatMessage;
    }
    public void setType(ReportType type) {
        this.type=type;
    }
    public void setStatus(ReportStatus status) {
        this.status=status;
    }
    public void setReason(String reason) {
        this.reason=reason;
    }
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt=createdAt;
    }
}