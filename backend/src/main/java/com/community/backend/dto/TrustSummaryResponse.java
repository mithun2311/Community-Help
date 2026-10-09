package com.community.backend.dto;
public class TrustSummaryResponse {
    private final Long userId;
    private final long totalRatings;
    private final double averageScore;
    private final String reliabilityLabel;
    public TrustSummaryResponse(Long userId,long totalRatings,double averageScore,String reliabilityLabel) {
        this.userId=userId; this.totalRatings=totalRatings; this.averageScore=averageScore; this.reliabilityLabel=reliabilityLabel;
    }
    public Long getUserId() { return userId; }
    public long getTotalRatings() { return totalRatings; }
    public double getAverageScore() { return averageScore; }
    public String getReliabilityLabel() { return reliabilityLabel; }
}
