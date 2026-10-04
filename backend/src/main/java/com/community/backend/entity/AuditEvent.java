package com.community.backend.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "audit_events")
public class AuditEvent {

    @Id
    @GeneratedValue
    private Long id;

    private Long helpRequestId;
    private String actorEmail;
    private String eventType;
    private String eventData;
    private LocalDateTime createdAt;
    private String previousHash;
    private String eventHash;

    public Long getId() {
        return id;
    }

    public Long getHelpRequestId() {
        return helpRequestId;
    }

    public String getActorEmail() {
        return actorEmail;
    }

    public String getEventType() {
        return eventType;
    }

    public String getEventData() {
        return eventData;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public String getPreviousHash() {
        return previousHash;
    }

    public String getEventHash() {
        return eventHash;
    }

    public void setHelpRequestId(Long helpRequestId) {
        this.helpRequestId = helpRequestId;
    }

    public void setActorEmail(String actorEmail) {
        this.actorEmail = actorEmail;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public void setEventData(String eventData) {
        this.eventData = eventData;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setPreviousHash(String previousHash) {
        this.previousHash = previousHash;
    }

    public void setEventHash(String eventHash) {
        this.eventHash = eventHash;
    }
}