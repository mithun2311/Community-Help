package com.community.backend.controller;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.community.backend.dto.TrackingLocationRequest;
import com.community.backend.dto.TrackingLocationResponse;
import com.community.backend.service.TrackingService;
@RestController
@RequestMapping("/api/help-requests")
public class TrackingController {
    private TrackingService trackingService;
    public TrackingController(TrackingService trackingService) {
        this.trackingService=trackingService;
    }
    @PostMapping("/{id}/tracking/location")
    public TrackingLocationResponse updateLocation(@PathVariable Long id,@Valid @RequestBody TrackingLocationRequest request) {
        return trackingService.updateLocation(id,request,getEmail());
    }
    @GetMapping("/{id}/tracking/location")
    public TrackingLocationResponse getLatestLocation(@PathVariable Long id) {
        return trackingService.getLatestLocation(id,getEmail());
    }
    private String getEmail() {
        Authentication authentication=SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }
}