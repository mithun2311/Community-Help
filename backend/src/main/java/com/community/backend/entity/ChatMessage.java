package com.community.backend.entity;
import java.time.LocalDateTime;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
@Entity
@Table(name="chat_messages")
public class ChatMessage {
    @Id
    @GeneratedValue
    private Long id;
    @ManyToOne
    @JoinColumn(name="chat_id")
    private Chat chat;
    @ManyToOne
    @JoinColumn(name="sender_id")
    private User sender;
    private String content;
    private LocalDateTime sentAt;
    public Long getId() {
        return id;
    }
    public Chat getChat() {
        return chat;
    }
    public User getSender() {
        return sender;
    }
    public String getContent() {
        return content;
    }
    public LocalDateTime getSentAt() {
        return sentAt;
    }
    public void setChat(Chat chat) {
        this.chat=chat;
    }
    public void setSender(User sender) {
        this.sender=sender;
    }
    public void setContent(String content) {
        this.content=content;
    }
    public void setSentAt(LocalDateTime sentAt) {
        this.sentAt=sentAt;
    }
}