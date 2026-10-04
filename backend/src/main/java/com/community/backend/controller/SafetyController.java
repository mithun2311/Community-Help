package com.community.backend.controller;
import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.community.backend.dto.TrustedContactRequest;
import com.community.backend.entity.SOSIncident;
import com.community.backend.entity.SafetyCheckIn;
import com.community.backend.entity.TrustedContact;
import com.community.backend.service.SafetyService;
@RestController
@RequestMapping("/api")
public class SafetyController {
    private SafetyService safetyService;
    public SafetyController(SafetyService safetyService) {
        this.safetyService=safetyService;
    }
    @PostMapping("/help-requests/{id}/safety-checkin")
    public SafetyCheckIn createCheckIn(@PathVariable Long id) {
        return safetyService.createCheckIn(id,getEmail());
    }
    @PutMapping("/safety-checkins/{id}/safe")
    public SafetyCheckIn markSafe(@PathVariable Long id) {
        return safetyService.markSafe(id,getEmail());
    }
    @GetMapping("/trusted-contacts")
    public List<TrustedContact> getTrustedContacts() {
        return safetyService.getTrustedContacts(getEmail());
    }
    @PostMapping("/trusted-contacts")
    public TrustedContact addTrustedContact(@RequestBody TrustedContactRequest request) {
        return safetyService.addTrustedContact(getEmail(),request.getName(),request.getPhone());
    }
    @DeleteMapping("/trusted-contacts/{id}")
    public void deleteTrustedContact(@PathVariable Long id) {
        safetyService.deleteTrustedContact(id,getEmail());
    }
    @PostMapping("/help-requests/{id}/sos")
    public SOSIncident triggerSOS(@PathVariable Long id) {
        return safetyService.triggerSOS(id,getEmail());
    }
    @PutMapping("/sos/{id}/resolve")
    public SOSIncident resolveSOS(@PathVariable Long id) {
        return safetyService.resolveSOS(id,getEmail());
    }
    private String getEmail() {
        Authentication authentication=SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }
}