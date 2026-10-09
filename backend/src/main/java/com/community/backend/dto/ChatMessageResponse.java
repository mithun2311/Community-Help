package com.community.backend.dto;
import java.time.LocalDateTime;
import com.community.backend.entity.ChatMessage;
public class ChatMessageResponse {
    private final Long id;
    private final Long chatId;
    private final Long senderId;
    private final String senderName;
    private final String content;
    private final LocalDateTime sentAt;
    public ChatMessageResponse(Long id,Long chatId,Long senderId,String senderName,String content,LocalDateTime sentAt) {
        this.id=id; this.chatId=chatId; this.senderId=senderId; this.senderName=senderName; this.content=content; this.sentAt=sentAt;
    }
    public static ChatMessageResponse from(ChatMessage message) { return new ChatMessageResponse(message.getId(),message.getChat().getId(),message.getSender().getId(),message.getSender().getName(),message.getContent(),message.getSentAt()); }
    public Long getId() { return id; }
    public Long getChatId() { return chatId; }
    public Long getSenderId() { return senderId; }
    public String getSenderName() { return senderName; }
    public String getContent() { return content; }
    public LocalDateTime getSentAt() { return sentAt; }
}
