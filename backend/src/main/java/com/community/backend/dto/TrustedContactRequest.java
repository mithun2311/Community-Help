package com.community.backend.dto;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
public class TrustedContactRequest {
    @NotBlank(message="Trusted contact name is required")
    @Size(max=100,message="Trusted contact name cannot exceed 100 characters")
    private String name;
    @NotBlank(message="Trusted contact phone is required")
    @Pattern(regexp="^[+0-9() .-]{7,25}$",message="Enter a valid phone number")
    private String phone;
    @Email(message="Trusted contact email must be valid")
    @Size(max=254,message="Trusted contact email cannot exceed 254 characters")
    private String email;
    public String getName() { return name; }
    public String getPhone() { return phone; }
    public String getEmail() { return email; }
    public void setName(String name) { this.name=name; }
    public void setPhone(String phone) { this.phone=phone; }
    public void setEmail(String email) { this.email=email; }
}
