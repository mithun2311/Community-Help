package com.community.backend.dto;
public class HelperProfileResponse {
    private final Long userId;
    private final String skills;
    private final boolean available;
    private final double maxRadiusKm;
    public HelperProfileResponse(Long userId,String skills,boolean available,double maxRadiusKm) {
        this.userId=userId; this.skills=skills; this.available=available; this.maxRadiusKm=maxRadiusKm;
    }
    public Long getUserId() { return userId; }
    public String getSkills() { return skills; }
    public boolean isAvailable() { return available; }
    public double getMaxRadiusKm() { return maxRadiusKm; }
}
