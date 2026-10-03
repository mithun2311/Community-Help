package com.community.backend.dto;
import com.community.backend.entity.HelpCategory;
import com.community.backend.entity.HelpUrgency;

public class CreateHelpRequest {
    private String title;
    private String description;
    private HelpCategory category;
    private HelpUrgency urgency;
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
}
