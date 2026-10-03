package com.community.backend.dto;
import com.community.backend.entity.HelpUrgency;
import com.community.backend.exception.ResourceNotFoundException;
import com.community.backend.entity.HelpRequest;
import java.util.Optional;
import com.community.backend.service.HelpRequestService;

public class UpdateHelpRequest {
    private String title;
    private String description;
    private HelpUrgency urgency;

    public String getTitle() {
        return title;
    }
    public String getDescription() {
        return description;
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
    public void setUrgency(HelpUrgency urgency) {
        this.urgency=urgency;
    }
}
 