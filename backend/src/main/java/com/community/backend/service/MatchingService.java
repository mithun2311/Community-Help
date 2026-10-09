package com.community.backend.service;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import com.community.backend.dto.HelpRequestMatchResponse;
import com.community.backend.entity.HelpCategory;
import com.community.backend.entity.HelpRequest;
import com.community.backend.entity.HelpRequestLocation;
import com.community.backend.entity.HelpRequestStatus;
import com.community.backend.entity.HelpUrgency;
import com.community.backend.entity.VerificationStatus;
import com.community.backend.exception.ResourceNotFoundException;
import com.community.backend.entity.HelperProfile;
import com.community.backend.repository.HelperProfileRepository;
import com.community.backend.repository.HelpRequestLocationRepository;
import com.community.backend.repository.HelpRequestRepository;
import com.community.backend.repository.UserRepository;
@Service
public class MatchingService {
    private final HelpRequestRepository helpRequestRepository;
    private final HelpRequestLocationRepository locationRepository;
    private final UserRepository userRepository;
    private final HelperProfileRepository helperProfileRepository;
    public MatchingService(HelpRequestRepository helpRequestRepository,HelpRequestLocationRepository locationRepository,UserRepository userRepository,HelperProfileRepository helperProfileRepository) {
        this.helpRequestRepository=helpRequestRepository;
        this.locationRepository=locationRepository;
        this.userRepository=userRepository;
        this.helperProfileRepository=helperProfileRepository;
    }
    public List<HelpRequestMatchResponse> findMatches(String email,double latitude,double longitude,double radiusKm,HelpCategory category) {
        if(!Double.isFinite(latitude) || latitude < -90 || latitude > 90 || !Double.isFinite(longitude) || longitude < -180 || longitude > 180) throw new IllegalArgumentException("A valid current latitude and longitude are required");
        if(!Double.isFinite(radiusKm) || radiusKm <= 0 || radiusKm > 100) throw new IllegalArgumentException("Radius must be greater than 0 and no more than 100 km");
        com.community.backend.entity.User helper=userRepository.findByEmail(email).orElseThrow(()->new ResourceNotFoundException("User not found"));
        if(helper.getVerificationStatus()!=VerificationStatus.VERIFIED) throw new com.community.backend.exception.UnauthorizedException("Account verification is required to view help matches");
        HelperProfile profile=helperProfileRepository.findByUser(helper).orElseThrow(()->new IllegalStateException("Configure a helper profile before viewing matches"));
        if(!profile.isAvailable()) throw new IllegalStateException("Enable helper availability before viewing matches");
        if(radiusKm>profile.getMaxRadiusKm()) radiusKm=profile.getMaxRadiusKm();
        List<String> helperSkills=java.util.Arrays.stream(profile.getSkills().split(",")).map(String::trim).filter(value->!value.isEmpty()).toList();
        List<HelpRequestMatchResponse> matches=new ArrayList<>();
        for(HelpRequest request:helpRequestRepository.findAllByStatusOrderByCreatedAtDesc(HelpRequestStatus.OPEN)) {
            if(request.getCreator().getEmail().equals(email)) continue;
            if(request.getExpiresAt()!=null && !request.getExpiresAt().isAfter(LocalDateTime.now())) continue;
            if(category!=null && request.getCategory()!=category) continue;
            if(!helperSkills.contains("ALL") && !helperSkills.contains(request.getCategory().name())) continue;
            Optional<HelpRequestLocation> optionalLocation=locationRepository.findByHelpRequest(request);
            if(optionalLocation.isEmpty()) continue;
            HelpRequestLocation location=optionalLocation.get();
            double distance=distanceKm(latitude,longitude,location.getApproximateLatitude(),location.getApproximateLongitude());
            if(distance>radiusKm) continue;
            int score=0;
            List<String> reasons=new ArrayList<>();
            if(request.getUrgency()==HelpUrgency.HIGH) { score+=50; reasons.add("High urgency (+50)"); }
            else if(request.getUrgency()==HelpUrgency.MEDIUM) { score+=30; reasons.add("Medium urgency (+30)"); }
            else { score+=15; reasons.add("Standard urgency (+15)"); }
            int distanceScore=(int)Math.round(30*Math.max(0,1-distance/radiusKm));
            score+=distanceScore;
            reasons.add(String.format(java.util.Locale.ROOT,"Approximate location is %.2f km away (+%d)",distance,distanceScore));
            long ageHours=Math.max(0,Duration.between(request.getCreatedAt(),LocalDateTime.now()).toHours());
            int freshnessScore=ageHours<1?20:ageHours<6?15:ageHours<12?10:ageHours<24?5:0;
            score+=freshnessScore;
            if(freshnessScore>0) reasons.add("Recently posted ( +"+freshnessScore+")");
            if(helperSkills.contains(request.getCategory().name()) || helperSkills.contains("ALL")) reasons.add("Matches helper skill profile");
            if(category!=null) reasons.add("Matches selected category");
            matches.add(new HelpRequestMatchResponse(request.getId(),request.getTitle(),request.getDescription(),request.getCategory(),request.getUrgency(),request.getStatus(),Math.round(distance*100.0)/100.0,score,reasons,request.getCreatedAt()));
        }
        matches.sort(Comparator.comparingInt(HelpRequestMatchResponse::getMatchScore).reversed().thenComparingDouble(HelpRequestMatchResponse::getDistanceKm).thenComparing(HelpRequestMatchResponse::getCreatedAt,Comparator.reverseOrder()));
        return matches;
    }
    private double distanceKm(double lat1,double lon1,double lat2,double lon2) {
        double earthRadius=6371.0088;
        double dLat=Math.toRadians(lat2-lat1);
        double dLon=Math.toRadians(lon2-lon1);
        double a=Math.sin(dLat/2)*Math.sin(dLat/2)+Math.cos(Math.toRadians(lat1))*Math.cos(Math.toRadians(lat2))*Math.sin(dLon/2)*Math.sin(dLon/2);
        return earthRadius*2*Math.atan2(Math.sqrt(a),Math.sqrt(Math.max(0,1-a)));
    }
}
