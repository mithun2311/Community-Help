package com.community.backend.dto;
public class LocationResponse {
    private Long requestId;
    private String accessLevel;
    private Double latitude;
    private Double longitude;
    private Integer privacyRadiusMeters;
    public Long getRequestId() { return requestId; }
    public String getAccessLevel() { return accessLevel; }
    public Double getLatitude() { return latitude; }
    public Double getLongitude() { return longitude; }
    public Integer getPrivacyRadiusMeters() { return privacyRadiusMeters; }
    public void setRequestId(Long requestId) { this.requestId=requestId; }
    public void setAccessLevel(String accessLevel) { this.accessLevel=accessLevel; }
    public void setLatitude(Double latitude) { this.latitude=latitude; }
    public void setLongitude(Double longitude) { this.longitude=longitude; }
    public void setPrivacyRadiusMeters(Integer privacyRadiusMeters) { this.privacyRadiusMeters=privacyRadiusMeters; }
}