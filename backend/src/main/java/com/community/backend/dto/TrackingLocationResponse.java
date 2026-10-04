package com.community.backend.dto;
import java.time.LocalDateTime;
public class TrackingLocationResponse {
    private Long requestId;
    private Double latitude;
    private Double longitude;
    private LocalDateTime recordedAt;
    private boolean stale;
    public TrackingLocationResponse(Long requestId,Double latitude,Double longitude,LocalDateTime recordedAt,boolean stale) {
        this.requestId=requestId;
        this.latitude=latitude;
        this.longitude=longitude;
        this.recordedAt=recordedAt;
        this.stale=stale;
    }
    public Long getRequestId() {
        return requestId;
    }
    public Double getLatitude() {
        return latitude;
    }
    public Double getLongitude() {
        return longitude;
    }
    public LocalDateTime getRecordedAt() {
        return recordedAt;
    }
    public boolean isStale() {
        return stale;
    }
}