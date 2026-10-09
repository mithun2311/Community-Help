package com.community.backend.entity;
import java.time.LocalDateTime;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
@Entity
@Table(name="help_request_images")
public class HelpRequestImage {
    @Id
    @GeneratedValue
    private Long id;
    @ManyToOne
    @JoinColumn(name="help_request_id",nullable=false)
    private HelpRequest helpRequest;
    private String imageUrl;
    private LocalDateTime createdAt;
    public Long getId() { return id; }
    public HelpRequest getHelpRequest() { return helpRequest; }
    public String getImageUrl() { return imageUrl; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setHelpRequest(HelpRequest helpRequest) { this.helpRequest=helpRequest; }
    public void setImageUrl(String imageUrl) { this.imageUrl=imageUrl; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt=createdAt; }
}
