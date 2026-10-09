package com.community.backend.service;
import java.util.Locale;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.community.backend.dto.HelperProfileRequest;
import com.community.backend.dto.HelperProfileResponse;
import com.community.backend.dto.ProfileResponse;
import com.community.backend.dto.TrustSummaryResponse;
import com.community.backend.entity.Rating;
import com.community.backend.repository.RatingRepository;
import com.community.backend.entity.HelperProfile;
import com.community.backend.entity.User;
import com.community.backend.entity.VerificationStatus;
import com.community.backend.exception.ResourceNotFoundException;
import com.community.backend.repository.HelperProfileRepository;
import com.community.backend.repository.UserRepository;
@Service
public class ProfileService {
    private final UserRepository userRepository;
    private final HelperProfileRepository helperProfileRepository;
    private final RatingRepository ratingRepository;
    public ProfileService(UserRepository userRepository,HelperProfileRepository helperProfileRepository,RatingRepository ratingRepository) {
        this.userRepository=userRepository; this.helperProfileRepository=helperProfileRepository; this.ratingRepository=ratingRepository;
    }
    public ProfileResponse getMyProfile(String email) {
        User user=getUser(email);
        return new ProfileResponse(user.getId(),user.getName(),user.getEmail(),user.getRole(),user.getVerificationStatus(),user.getAccountType(),user.getOrganizationName());
    }
    @Transactional
    public HelperProfileResponse saveHelperProfile(String email,HelperProfileRequest request) {
        User user=getUser(email);
        if(user.getVerificationStatus()!=VerificationStatus.VERIFIED) throw new com.community.backend.exception.UnauthorizedException("Account verification is required to configure a helper profile");
        String skills=normalizeSkills(request.getSkills());
        HelperProfile profile=helperProfileRepository.findByUser(user).orElseGet(HelperProfile::new);
        profile.setUser(user);
        profile.setSkills(skills);
        profile.setAvailable(request.getAvailable());
        profile.setMaxRadiusKm(request.getMaxRadiusKm());
        return toResponse(helperProfileRepository.save(profile));
    }
    public HelperProfileResponse getHelperProfile(String email) {
        User user=getUser(email);
        HelperProfile profile=helperProfileRepository.findByUser(user).orElseThrow(()->new ResourceNotFoundException("Helper profile not found"));
        return toResponse(profile);
    }
    public TrustSummaryResponse getTrustSummary(Long userId) {
        User user=userRepository.findById(userId).orElseThrow(()->new ResourceNotFoundException("User not found"));
        java.util.List<Rating> ratings=ratingRepository.findByRatedUserOrderByCreatedAtDesc(user);
        double average=ratings.stream().mapToInt(Rating::getScore).average().orElse(0.0);
        String label=ratings.isEmpty()?"NEW":average>=4.5?"HIGH_TRUST":average>=3.5?"ESTABLISHED":"NEEDS_ATTENTION";
        return new TrustSummaryResponse(user.getId(),ratings.size(),Math.round(average*100.0)/100.0,label);
    }
    private String normalizeSkills(String skills) {
        String normalized=java.util.Arrays.stream(skills.split(",")).map(String::trim).filter(value->!value.isEmpty()).map(value->value.toUpperCase(Locale.ROOT)).distinct().collect(java.util.stream.Collectors.joining(","));
        if(normalized.isBlank()) throw new IllegalArgumentException("At least one skill or category is required");
        return normalized;
    }
    private HelperProfileResponse toResponse(HelperProfile profile) { return new HelperProfileResponse(profile.getUser().getId(),profile.getSkills(),profile.isAvailable(),profile.getMaxRadiusKm()); }
    private User getUser(String email) { return userRepository.findByEmail(email).orElseThrow(()->new ResourceNotFoundException("User not found")); }
}
