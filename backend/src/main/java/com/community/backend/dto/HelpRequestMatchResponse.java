package com.community.backend.dto;
import java.time.LocalDateTime;
import java.util.List;
import com.community.backend.entity.HelpCategory;
import com.community.backend.entity.HelpRequestStatus;
import com.community.backend.entity.HelpUrgency;
public class HelpRequestMatchResponse {
    private Long id;
    private String title;
    private String description;
    private HelpCategory category;
    private HelpUrgency urgency;
    private HelpRequestStatus status;
    private double distanceKm;
    private int matchScore;
    private List<String> reasons;
    private LocalDateTime createdAt;
    public HelpRequestMatchResponse(Long id,String title,String description,HelpCategory category,HelpUrgency urgency,HelpRequestStatus status,double distanceKm,int matchScore,List<String> reasons,LocalDateTime createdAt) {
        this.id=id;
        this.title=title;
        this.description=description;
        this.category=category;
        this.urgency=urgency;
        this.status=status;
        this.distanceKm=distanceKm;
        this.matchScore=matchScore;
        this.reasons=reasons;
        this.createdAt=createdAt;
    }
    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public HelpCategory getCategory() { return category; }
    public HelpUrgency getUrgency() { return urgency; }
    public HelpRequestStatus getStatus() { return status; }
    public double getDistanceKm() { return distanceKm; }
    public int getMatchScore() { return matchScore; }
    public List<String> getReasons() { return reasons; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
