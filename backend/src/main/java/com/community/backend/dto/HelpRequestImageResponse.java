package com.community.backend.dto;
import java.time.LocalDateTime;
public class HelpRequestImageResponse {
    private final Long id;
    private final Long helpRequestId;
    private final String imageUrl;
    private final LocalDateTime createdAt;
    public HelpRequestImageResponse(Long id,Long helpRequestId,String imageUrl,LocalDateTime createdAt) { this.id=id; this.helpRequestId=helpRequestId; this.imageUrl=imageUrl; this.createdAt=createdAt; }
    public Long getId() { return id; }
    public Long getHelpRequestId() { return helpRequestId; }
    public String getImageUrl() { return imageUrl; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
