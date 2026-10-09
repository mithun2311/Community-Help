package com.community.backend.entity;
import java.time.LocalDateTime;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
@Entity
@Table(name="idempotency_records",uniqueConstraints=@UniqueConstraint(name="uk_idempotency_user_operation_key",columnNames={"user_email","operation","idempotency_key"}))
public class IdempotencyRecord {
    @Id
    @GeneratedValue
    private Long id;
    @Column(name="user_email",nullable=false,length=254)
    private String userEmail;
    @Column(nullable=false,length=80)
    private String operation;
    @Column(name="idempotency_key",nullable=false,length=100)
    private String idempotencyKey;
    @Column(nullable=false)
    private Long resourceId;
    @Column(nullable=false)
    private LocalDateTime createdAt;
    public Long getId() { return id; }
    public String getUserEmail() { return userEmail; }
    public String getOperation() { return operation; }
    public String getIdempotencyKey() { return idempotencyKey; }
    public Long getResourceId() { return resourceId; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setUserEmail(String userEmail) { this.userEmail=userEmail; }
    public void setOperation(String operation) { this.operation=operation; }
    public void setIdempotencyKey(String idempotencyKey) { this.idempotencyKey=idempotencyKey; }
    public void setResourceId(Long resourceId) { this.resourceId=resourceId; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt=createdAt; }
}
