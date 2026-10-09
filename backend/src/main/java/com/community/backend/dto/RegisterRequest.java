package com.community.backend.dto;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import com.community.backend.entity.AccountType;

public class RegisterRequest {
    @NotBlank(message="Name is required")
    @Size(max=100,message="Name cannot exceed 100 characters")
    private String name;
    @NotBlank(message="Email is required")
    @Email(message="Email must be valid")
    @Size(max=254,message="Email cannot exceed 254 characters")
    private String email;
    private AccountType accountType=AccountType.INDIVIDUAL;
    @Size(max=160,message="Organization name cannot exceed 160 characters")
    private String organizationName;
    @NotBlank(message="Password is required")
    @Size(min=6,max=72,message="Password must contain between 6 and 72 characters")
    private String password;

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public AccountType getAccountType() { return accountType; }

    public String getOrganizationName() { return organizationName; }

    public void setAccountType(AccountType accountType) { this.accountType=accountType; }

    public void setOrganizationName(String organizationName) { this.organizationName=organizationName; }

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
