package com.community.backend.entity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
@Entity
@Table(name="helper_profiles")
public class HelperProfile {
    @Id
    @GeneratedValue
    private Long id;
    @OneToOne
    @JoinColumn(name="user_id",unique=true,nullable=false)
    private User user;
    @Column(length=500,nullable=false)
    private String skills;
    private boolean available;
    private double maxRadiusKm;
    public Long getId() { return id; }
    public User getUser() { return user; }
    public String getSkills() { return skills; }
    public boolean isAvailable() { return available; }
    public double getMaxRadiusKm() { return maxRadiusKm; }
    public void setUser(User user) { this.user=user; }
    public void setSkills(String skills) { this.skills=skills; }
    public void setAvailable(boolean available) { this.available=available; }
    public void setMaxRadiusKm(double maxRadiusKm) { this.maxRadiusKm=maxRadiusKm; }
}
