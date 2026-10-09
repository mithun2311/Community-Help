package com.community.backend.dto;
import java.time.LocalDateTime;
import com.community.backend.entity.Chat;
import com.community.backend.entity.ChatStatus;
public class ChatResponse {
    private final Long id;
    private final Long helpRequestId;
    private final Long requesterId;
    private final Long helperId;
    private final ChatStatus status;
    private final LocalDateTime createdAt;
    private final LocalDateTime endedAt;
    public ChatResponse(Long id,Long helpRequestId,Long requesterId,Long helperId,ChatStatus status,LocalDateTime createdAt,LocalDateTime endedAt) {
        this.id=id; this.helpRequestId=helpRequestId; this.requesterId=requesterId; this.helperId=helperId; this.status=status; this.createdAt=createdAt; this.endedAt=endedAt;
    }
    public static ChatResponse from(Chat chat) { return new ChatResponse(chat.getId(),chat.getHelpRequest().getId(),chat.getRequester().getId(),chat.getHelper().getId(),chat.getStatus(),chat.getCreatedAt(),chat.getEndedAt()); }
    public Long getId() { return id; }
    public Long getHelpRequestId() { return helpRequestId; }
    public Long getRequesterId() { return requesterId; }
    public Long getHelperId() { return helperId; }
    public ChatStatus getStatus() { return status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getEndedAt() { return endedAt; }
}
