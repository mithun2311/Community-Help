package com.community.backend.entity;
import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
@Entity
@Table(name="sos_incidents")
public class SOSIncident {
    @Id
    @GeneratedValue
    private Long id;
    @ManyToOne
    @JoinColumn(name="help_request_id")
    private HelpRequest helpRequest;
    @ManyToOne
    @JoinColumn(name="triggered_by")
    private User triggeredBy;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime resolvedAt;
    public Long getId() {
        return id;
    }
    public HelpRequest getHelpRequest() {
        return helpRequest;
    }
    public User getTriggeredBy() {
        return triggeredBy;
    }
    public String getStatus() {
        return status;
    }
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    public LocalDateTime getResolvedAt() {
        return resolvedAt;
    }
    public void setHelpRequest(HelpRequest helpRequest) {
        this.helpRequest=helpRequest;
    }
    public void setTriggeredBy(User triggeredBy) {
        this.triggeredBy=triggeredBy;
    }
    public void setStatus(String status) {
        this.status=status;
    }
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt=createdAt;
    }
    public void setResolvedAt(LocalDateTime resolvedAt) {
        this.resolvedAt=resolvedAt;
    }
}