package com.community.backend.controller;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.community.backend.dto.HelperProfileRequest;
import com.community.backend.dto.HelperProfileResponse;
import com.community.backend.dto.ProfileResponse;
import com.community.backend.dto.TrustSummaryResponse;
import com.community.backend.service.ProfileService;
import jakarta.validation.Valid;
@RestController
@RequestMapping("/api/profile")
public class ProfileController {
    private final ProfileService profileService;
    public ProfileController(ProfileService profileService) { this.profileService=profileService; }
    @GetMapping
    public ProfileResponse getMyProfile(Authentication authentication) { return profileService.getMyProfile(authentication.getName()); }
    @GetMapping("/users/{userId}/trust")
    public TrustSummaryResponse getTrustSummary(@PathVariable Long userId) { return profileService.getTrustSummary(userId); }
    @GetMapping("/helper")
    public HelperProfileResponse getHelperProfile(Authentication authentication) { return profileService.getHelperProfile(authentication.getName()); }
    @PutMapping("/helper")
    public HelperProfileResponse saveHelperProfile(@Valid @RequestBody HelperProfileRequest request,Authentication authentication) { return profileService.saveHelperProfile(authentication.getName(),request); }
}
