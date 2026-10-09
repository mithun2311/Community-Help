package com.community.backend.dto;
public class TrustedContactResponse {
    private final Long id;
    private final String name;
    private final String phone;
    private final String email;
    public TrustedContactResponse(Long id,String name,String phone,String email) {
        this.id=id; this.name=name; this.phone=phone; this.email=email;
    }
    public Long getId() { return id; }
    public String getName() { return name; }
    public String getPhone() { return phone; }
    public String getEmail() { return email; }
}
