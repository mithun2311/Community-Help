package com.community.backend.dto;
import java.time.LocalDateTime;
import com.community.backend.entity.VerificationStatus;
public class VerificationResponse {
    private final VerificationStatus status;
    private final String note;
    private final LocalDateTime requestedAt;
    public VerificationResponse(VerificationStatus status,String note,LocalDateTime requestedAt) {
        this.status=status;
        this.note=note;
        this.requestedAt=requestedAt;
    }
    public VerificationStatus getStatus() { return status; }
    public String getNote() { return note; }
    public LocalDateTime getRequestedAt() { return requestedAt; }
}
