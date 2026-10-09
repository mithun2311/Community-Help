package com.community.backend.controller;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.community.backend.dto.VerificationRequest;
import com.community.backend.dto.VerificationResponse;
import com.community.backend.service.VerificationService;
import jakarta.validation.Valid;
@RestController
@RequestMapping("/api/profile/verification")
public class VerificationController {
    private final VerificationService verificationService;
    public VerificationController(VerificationService verificationService) { this.verificationService=verificationService; }
    @GetMapping
    public VerificationResponse getStatus(Authentication authentication) { return verificationService.getStatus(authentication.getName()); }
    @PostMapping
    public VerificationResponse requestVerification(@Valid @RequestBody VerificationRequest request,Authentication authentication) { return verificationService.requestVerification(authentication.getName(),request); }
}
