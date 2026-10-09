package com.community.backend.dto;
import java.util.List;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
public class UpdateHelpRequestTagsRequest {
    @NotEmpty(message="At least one tag is required")
    @Size(max=10,message="A request can have at most 10 tags")
    private List<@jakarta.validation.constraints.NotBlank @Size(max=40) String> tags;
    public List<String> getTags() { return tags; }
    public void setTags(List<String> tags) { this.tags=tags; }
}
