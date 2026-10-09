package com.community.backend.entity;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
@Entity
@Table(name="trusted_contacts")
public class TrustedContact {
    @Id
    @GeneratedValue
    private Long id;
    @ManyToOne
    @JoinColumn(name="user_id")
    private User user;
    private String name;
    private String phone;
    private String email;
    public Long getId() {
        return id;
    }
    public User getUser() {
        return user;
    }
    public String getName() {
        return name;
    }
    public String getPhone() {
        return phone;
    }
    public String getEmail() {
        return email;
    }
    public void setUser(User user) {
        this.user=user;
    }
    public void setName(String name) {
        this.name=name;
    }
    public void setPhone(String phone) {
        this.phone=phone;
    }
    public void setEmail(String email) {
        this.email=email;
    }
}