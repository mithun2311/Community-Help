package com.community.backend.dto;
import com.community.backend.entity.UserRole;
import com.community.backend.entity.AccountType;
import com.community.backend.entity.VerificationStatus;
public class ProfileResponse {
    private final Long id;
    private final String name;
    private final String email;
    private final UserRole role;
    private final VerificationStatus verificationStatus;
    private final AccountType accountType;
    private final String organizationName;
    public ProfileResponse(Long id,String name,String email,UserRole role,VerificationStatus verificationStatus,AccountType accountType,String organizationName) {
        this.id=id; this.name=name; this.email=email; this.role=role; this.verificationStatus=verificationStatus; this.accountType=accountType; this.organizationName=organizationName;
    }
    public Long getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public UserRole getRole() { return role; }
    public VerificationStatus getVerificationStatus() { return verificationStatus; }
    public AccountType getAccountType() { return accountType; }
    public String getOrganizationName() { return organizationName; }
}
