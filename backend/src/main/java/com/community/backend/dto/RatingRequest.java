package com.community.backend.dto;
public class RatingRequest {
    private Integer score;
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