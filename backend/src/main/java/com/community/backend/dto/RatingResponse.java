package com.community.backend.dto;
import java.time.LocalDateTime;
import com.community.backend.entity.Rating;
public class RatingResponse {
    private final Long id;
    private final Long helpRequestId;
    private final Long raterId;
    private final Long ratedUserId;
    private final Integer score;
    private final String feedback;
    private final LocalDateTime createdAt;
    public RatingResponse(Long id,Long helpRequestId,Long raterId,Long ratedUserId,Integer score,String feedback,LocalDateTime createdAt) {
        this.id=id; this.helpRequestId=helpRequestId; this.raterId=raterId; this.ratedUserId=ratedUserId; this.score=score; this.feedback=feedback; this.createdAt=createdAt;
    }
    public static RatingResponse from(Rating rating) { return new RatingResponse(rating.getId(),rating.getHelpRequest().getId(),rating.getRater().getId(),rating.getRatedUser().getId(),rating.getScore(),rating.getFeedback(),rating.getCreatedAt()); }
    public Long getId() { return id; }
    public Long getHelpRequestId() { return helpRequestId; }
    public Long getRaterId() { return raterId; }
    public Long getRatedUserId() { return ratedUserId; }
    public Integer getScore() { return score; }
    public String getFeedback() { return feedback; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
