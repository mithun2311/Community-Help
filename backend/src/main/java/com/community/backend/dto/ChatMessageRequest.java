package com.community.backend.dto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
public class ChatMessageRequest {
    @NotBlank(message="Message cannot be empty")
    @Size(max=2000,message="Message cannot exceed 2000 characters")
    private String content;
    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content=content;
    }
}
