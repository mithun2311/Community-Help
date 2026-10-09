package com.community.backend.dto;
import java.time.LocalDateTime;
import com.community.backend.entity.AccountType;
import com.community.backend.entity.UserRole;
import com.community.backend.entity.VerificationStatus;
public class AdminUserResponse {
    private final Long id;
    private final String name;
    private final String email;
    private final UserRole role;
    private final AccountType accountType;
    private final String organizationName;
    private final VerificationStatus verificationStatus;
    private final String verificationNote;
    private final LocalDateTime verificationRequestedAt;
    public AdminUserResponse(Long id,String name,String email,UserRole role,AccountType accountType,String organizationName,VerificationStatus verificationStatus,String verificationNote,LocalDateTime verificationRequestedAt) {
        this.id=id; this.name=name; this.email=email; this.role=role; this.accountType=accountType; this.organizationName=organizationName; this.verificationStatus=verificationStatus; this.verificationNote=verificationNote; this.verificationRequestedAt=verificationRequestedAt;
    }
    public Long getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public UserRole getRole() { return role; }
    public AccountType getAccountType() { return accountType; }
    public String getOrganizationName() { return organizationName; }
    public VerificationStatus getVerificationStatus() { return verificationStatus; }
    public String getVerificationNote() { return verificationNote; }
    public LocalDateTime getVerificationRequestedAt() { return verificationRequestedAt; }
}
