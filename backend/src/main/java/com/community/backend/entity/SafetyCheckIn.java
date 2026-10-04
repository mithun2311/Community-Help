package com.community.backend.entity;
import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
@Entity
@Table(name="safety_checkins")
public class SafetyCheckIn {
    @Id
    @GeneratedValue
    private Long id;
    @ManyToOne
    @JoinColumn(name="help_request_id")
    private HelpRequest helpRequest;
    @ManyToOne
    @JoinColumn(name="user_id")
    private User user;
    @Enumerated(EnumType.STRING)
    private SafetyCheckInStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime respondedAt;
    public Long getId() {
        return id;
    }
    public HelpRequest getHelpRequest() {
        return helpRequest;
    }
    public User getUser() {
        return user;
    }
    public SafetyCheckInStatus getStatus() {
        return status;
    }
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    public LocalDateTime getRespondedAt() {
        return respondedAt;
    }
    public void setHelpRequest(HelpRequest helpRequest) {
        this.helpRequest=helpRequest;
    }
    public void setUser(User user) {
        this.user=user;
    }
    public void setStatus(SafetyCheckInStatus status) {
        this.status=status;
    }
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt=createdAt;
    }
    public void setRespondedAt(LocalDateTime respondedAt) {
        this.respondedAt=respondedAt;
    }
}