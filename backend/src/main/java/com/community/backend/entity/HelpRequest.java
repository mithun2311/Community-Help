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
import jakarta.persistence.Version;
@Entity
@Table(name="help_requests")
public class HelpRequest {
    @Id
    @GeneratedValue
    private Long id;
    @Version
    private Long version;
    private String title;
    private String description;
    @Enumerated(EnumType.STRING)
    private HelpCategory category;
    @Enumerated(EnumType.STRING)
    private HelpUrgency urgency;
    @Enumerated(EnumType.STRING)
    private HelpRequestStatus status;
    @ManyToOne
    @JoinColumn(name="creator_id")
    private User creator;
    @ManyToOne
    @JoinColumn(name="helper_id")
    private User helper;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    public Long getId() {
        return id;
    }
    public String getTitle() {
        return title;
    }
    public String getDescription() {
        return description;
    }
    public HelpCategory getCategory() {
        return category;
    }
    public HelpUrgency getUrgency() {
        return urgency;
    }
    public HelpRequestStatus getStatus() {
        return status;
    }
    public User getCreator() {
        return creator;
    }
    public User getHelper() {
        return helper;
    }
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    public void setTitle(String title) {
        this.title=title;
    }
    public void setDescription(String description) {
        this.description=description;
    }
    public void setCategory(HelpCategory category) {
        this.category=category;
    }
    public void setUrgency(HelpUrgency urgency) {
        this.urgency=urgency;
    }
    public void setStatus(HelpRequestStatus status) {
        this.status=status;
    }
    public void setCreator(User creator) {
        this.creator=creator;
    }
    public void setHelper(User helper) {
        this.helper=helper;
    }
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt=createdAt;
    }
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt=updatedAt;
    }
}