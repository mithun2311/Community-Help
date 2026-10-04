package com.community.backend.dto;
import java.time.LocalDateTime;
public class SOSResponse {
    private Long id;
    private Long requestId;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime resolvedAt;
    public SOSResponse(Long id,Long requestId,String status,LocalDateTime createdAt,LocalDateTime resolvedAt) {
        this.id=id;
        this.requestId=requestId;
        this.status=status;
        this.createdAt=createdAt;
        this.resolvedAt=resolvedAt;
    }
    public Long getId() {
        return id;
    }
    public Long getRequestId() {
        return requestId;
    }
    public String getStatus() {
        return status;
    }
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    public LocalDateTime getResolvedAt() {
        return resolvedAt;
    }
}