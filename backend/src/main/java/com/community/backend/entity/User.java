package com.community.backend.entity;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.PrePersist;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonIgnore;
@Entity
@Table(name="users")
public class User {
    @Id
    @GeneratedValue
    private Long id;
    private String name;
    @Column(unique=true)
    private String email;
    @JsonIgnore
    private String password;
    @Enumerated(EnumType.STRING)
    @Column
    private UserRole role=UserRole.USER;
    @Enumerated(EnumType.STRING)
    @Column
    private AccountType accountType=AccountType.INDIVIDUAL;
    @Column(length=160)
    private String organizationName;
    @Enumerated(EnumType.STRING)
    @Column
    private VerificationStatus verificationStatus=VerificationStatus.UNVERIFIED;
    @Column(length=500)
    private String verificationNote;
    private LocalDateTime verificationRequestedAt;

    @PrePersist
    public void applyDefaults() {
        if(role==null) role=UserRole.USER;
        if(accountType==null) accountType=AccountType.INDIVIDUAL;
        if(verificationStatus==null) verificationStatus=VerificationStatus.UNVERIFIED;
    }

    public UserRole getRole() {
        return role==null?UserRole.USER:role;
    }

    public AccountType getAccountType() {
        return accountType==null?AccountType.INDIVIDUAL:accountType;
    }

    public String getOrganizationName() { return organizationName; }

    public void setAccountType(AccountType accountType) { this.accountType=accountType; }

    public void setOrganizationName(String organizationName) { this.organizationName=organizationName; }

    public VerificationStatus getVerificationStatus() {
        return verificationStatus==null?VerificationStatus.UNVERIFIED:verificationStatus;
    }

    public void setRole(UserRole role) {
        this.role=role;
    }

    public void setVerificationStatus(VerificationStatus verificationStatus) {
        this.verificationStatus=verificationStatus;
    }

    public String getVerificationNote() {
        return verificationNote;
    }

    public LocalDateTime getVerificationRequestedAt() {
        return verificationRequestedAt;
    }

    public void setVerificationNote(String verificationNote) {
        this.verificationNote=verificationNote;
    }

    public void setVerificationRequestedAt(LocalDateTime verificationRequestedAt) {
        this.verificationRequestedAt=verificationRequestedAt;
    }

    public Long getId() {
        return id;
    }
    
    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public void setName(String name) {
        this.name=name;
    }

    public void setEmail(String email) {
        this.email=email;
    }

    public void setPassword(String password) {
        this.password=password;
    }
}
