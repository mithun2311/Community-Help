package com.community.backend.dto;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
public class RatingRequest {
    @NotNull(message="Rating score is required")
    @Min(value=1,message="Rating must be between 1 and 5")
    @Max(value=5,message="Rating must be between 1 and 5")
    private Integer score;
    @Size(max=1000,message="Feedback cannot exceed 1000 characters")
    private String feedback;
    public Integer getScore() {
        return score;
    }
    public String getFeedback() {
        return feedback;
    }
    public void setScore(Integer score) {
        this.score=score;
    }
    public void setFeedback(String feedback) {
        this.feedback=feedback;
    }
}
