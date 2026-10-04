package com.community.backend.entity;
import java.time.LocalDateTime;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
@Entity
@Table(name="ratings")
public class Rating {
    @Id
    @GeneratedValue
    private Long id;
    @ManyToOne
    @JoinColumn(name="help_request_id")
    private HelpRequest helpRequest;
    @ManyToOne
    @JoinColumn(name="rater_id")
    private User rater;
    @ManyToOne
    @JoinColumn(name="rated_user_id")
    private User ratedUser;
    private Integer score;
    private String feedback;
    private LocalDateTime createdAt;
    public Long getId() {
        return id;
    }
    public HelpRequest getHelpRequest() {
        return helpRequest;
    }
    public User getRater() {
        return rater;
    }
    public User getRatedUser() {
        return ratedUser;
    }
    public Integer getScore() {
        return score;
    }
    public String getFeedback() {
        return feedback;
    }
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    public void setHelpRequest(HelpRequest helpRequest) {
        this.helpRequest=helpRequest;
    }
    public void setRater(User rater) {
        this.rater=rater;
    }
    public void setRatedUser(User ratedUser) {
        this.ratedUser=ratedUser;
    }
    public void setScore(Integer score) {
        this.score=score;
    }
    public void setFeedback(String feedback) {
        this.feedback=feedback;
    }
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt=createdAt;
    }
}