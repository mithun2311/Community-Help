package com.community.backend.dto;
import com.community.backend.entity.HelpCategory;
import com.community.backend.entity.HelpUrgency;
import com.community.backend.entity.HelpRequestStatus;

public class HelpRequestResponse {
    private Long id;
    private String title;
    private String description;
    private HelpCategory category;
    private HelpUrgency urgency;
    private HelpRequestStatus status;

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
    public void setId(Long id) {
        this.id=id;
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
}
