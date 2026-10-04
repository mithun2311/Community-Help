package com.community.backend.entity;
import java.time.LocalDateTime;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
@Entity
@Table(name="chats")
public class Chat {
    @Id
    @GeneratedValue
    private Long id;
    @OneToOne
    @JoinColumn(name="help_request_id",unique=true)
    private HelpRequest helpRequest;
    @ManyToOne
    @JoinColumn(name="requester_id")
    private User requester;
    @ManyToOne
    @JoinColumn(name="helper_id")
    private User helper;
    @Enumerated(EnumType.STRING)
    private ChatStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime endedAt;
    public Long getId() {
        return id;
    }
    public HelpRequest getHelpRequest() {
        return helpRequest;
    }
    public User getRequester() {
        return requester;
    }
    public User getHelper() {
        return helper;
    }
    public ChatStatus getStatus() {
        return status;
    }
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    public LocalDateTime getEndedAt() {
        return endedAt;
    }
    public void setHelpRequest(HelpRequest helpRequest) {
        this.helpRequest=helpRequest;
    }
    public void setRequester(User requester) {
        this.requester=requester;
    }
    public void setHelper(User helper) {
        this.helper=helper;
    }
    public void setStatus(ChatStatus status) {
        this.status=status;
    }
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt=createdAt;
    }
    public void setEndedAt(LocalDateTime endedAt) {
        this.endedAt=endedAt;
    }
}