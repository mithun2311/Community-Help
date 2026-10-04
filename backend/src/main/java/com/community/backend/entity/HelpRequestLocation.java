package com.community.backend.entity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.OneToOne;
import jakarta.persistence.JoinColumn;
@Entity
@Table(name="help_request_locations")
public class HelpRequestLocation {
    @Id
    @GeneratedValue
    private Long id;
    @OneToOne
    @JoinColumn(name="help_request_id",unique=true)
    private HelpRequest helpRequest;
    private Double exactLatitude;
    private Double exactLongitude;
    private Double approximateLatitude;
    private Double approximateLongitude;
    private Integer privacyRadiusMeters;
    public Long getId() {
        return id;
    }
    public HelpRequest getHelpRequest() {
        return helpRequest;
    }
    public Double getExactLatitude() {
        return exactLatitude;
    }
    public Double getExactLongitude() {
        return exactLongitude;
    }
    public Double getApproximateLatitude() {
        return approximateLatitude;
    }
    public Double getApproximateLongitude() {
        return approximateLongitude;
    }
    public Integer getPrivacyRadiusMeters() {
        return privacyRadiusMeters;
    }
    public void setHelpRequest(HelpRequest helpRequest) {
        this.helpRequest=helpRequest;
    }
    public void setExactLatitude(Double exactLatitude) {
        this.exactLatitude=exactLatitude;
    }
    public void setExactLongitude(Double exactLongitude) {
        this.exactLongitude=exactLongitude;
    }
    public void setApproximateLatitude(Double approximateLatitude) {
        this.approximateLatitude=approximateLatitude;
    }
    public void setApproximateLongitude(Double approximateLongitude) {
        this.approximateLongitude=approximateLongitude;
    }
    public void setPrivacyRadiusMeters(Integer privacyRadiusMeters) {
        this.privacyRadiusMeters=privacyRadiusMeters;
    }
}