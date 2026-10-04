package com.community.backend.entity;
import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
@Entity
@Table(name="tracking_locations")
public class TrackingLocation {
    @Id
    @GeneratedValue
    private Long id;
    @ManyToOne
    @JoinColumn(name="help_request_id")
    private HelpRequest helpRequest;
    @ManyToOne
    @JoinColumn(name="helper_id")
    private User helper;
    private Double latitude;
    private Double longitude;
    private LocalDateTime recordedAt;
    public Long getId() {
        return id;
    }
    public HelpRequest getHelpRequest() {
        return helpRequest;
    }
    public User getHelper() {
        return helper;
    }
    public Double getLatitude() {
        return latitude;
    }
    public Double getLongitude() {
        return longitude;
    }
    public LocalDateTime getRecordedAt() {
        return recordedAt;
    }
    public void setHelpRequest(HelpRequest helpRequest) {
        this.helpRequest=helpRequest;
    }
    public void setHelper(User helper) {
        this.helper=helper;
    }
    public void setLatitude(Double latitude) {
        this.latitude=latitude;
    }
    public void setLongitude(Double longitude) {
        this.longitude=longitude;
    }
    public void setRecordedAt(LocalDateTime recordedAt) {
        this.recordedAt=recordedAt;
    }
}