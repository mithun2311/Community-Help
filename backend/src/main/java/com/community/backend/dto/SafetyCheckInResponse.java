package com.community.backend.dto;
import java.time.LocalDateTime;
import com.community.backend.entity.SafetyCheckInStatus;
public class SafetyCheckInResponse {
    private Long id;
    private Long requestId;
    private SafetyCheckInStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime respondedAt;
    public SafetyCheckInResponse(Long id,Long requestId,SafetyCheckInStatus status,LocalDateTime createdAt,LocalDateTime respondedAt) {
        this.id=id;
        this.requestId=requestId;
        this.status=status;
        this.createdAt=createdAt;
        this.respondedAt=respondedAt;
    }
    public Long getId() {
        return id;
    }
    public Long getRequestId() {
        return requestId;
    }
    public SafetyCheckInStatus getStatus() {
        return status;
    }
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    public LocalDateTime getRespondedAt() {
        return respondedAt;
    }
}