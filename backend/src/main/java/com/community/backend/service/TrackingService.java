package com.community.backend.service;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.stereotype.Service;
import com.community.backend.dto.TrackingLocationRequest;
import com.community.backend.dto.TrackingLocationResponse;
import com.community.backend.entity.HelpRequest;
import com.community.backend.entity.HelpRequestStatus;
import com.community.backend.entity.TrackingLocation;
import com.community.backend.entity.User;
import com.community.backend.exception.ResourceNotFoundException;
import com.community.backend.exception.UnauthorizedException;
import com.community.backend.repository.HelpRequestRepository;
import com.community.backend.repository.TrackingLocationRepository;
import com.community.backend.repository.UserRepository;
@Service
public class TrackingService {
    private HelpRequestRepository helpRequestRepository;
    private TrackingLocationRepository trackingLocationRepository;
    private UserRepository userRepository;
    public TrackingService(HelpRequestRepository helpRequestRepository,TrackingLocationRepository trackingLocationRepository,UserRepository userRepository) {
        this.helpRequestRepository=helpRequestRepository;
        this.trackingLocationRepository=trackingLocationRepository;
        this.userRepository=userRepository;
    }
    public TrackingLocationResponse updateLocation(Long id,TrackingLocationRequest request,String email) {
        HelpRequest helpRequest=getRequest(id);
        User helper=getUser(email);
        if(helpRequest.getHelper()==null || !helpRequest.getHelper().getEmail().equals(email)) {
            throw new UnauthorizedException("Only the assigned helper can update tracking");
        }
        if(helpRequest.getStatus()!=HelpRequestStatus.EN_ROUTE && helpRequest.getStatus()!=HelpRequestStatus.ARRIVED && helpRequest.getStatus()!=HelpRequestStatus.IN_PROGRESS) {
            throw new IllegalStateException("Tracking is not active for this request");
        }
        if(request.getLatitude()==null || request.getLongitude()==null) {
            throw new IllegalArgumentException("Latitude and longitude are required");
        }
        if(request.getLatitude()<-90 || request.getLatitude()>90 || request.getLongitude()<-180 || request.getLongitude()>180) {
            throw new IllegalArgumentException("Invalid location");
        }
        TrackingLocation location=new TrackingLocation();
        location.setHelpRequest(helpRequest);
        location.setHelper(helper);
        location.setLatitude(request.getLatitude());
        location.setLongitude(request.getLongitude());
        location.setRecordedAt(LocalDateTime.now());
        location=trackingLocationRepository.save(location);
        return toResponse(location);
    }
    public TrackingLocationResponse getLatestLocation(Long id,String email) {
        HelpRequest helpRequest=getRequest(id);
        boolean creator=helpRequest.getCreator().getEmail().equals(email);
        boolean helper=helpRequest.getHelper()!=null && helpRequest.getHelper().getEmail().equals(email);
        if(!creator && !helper) {
            throw new UnauthorizedException("User is not authorized to access live tracking");
        }
        if(helpRequest.getStatus()==HelpRequestStatus.COMPLETED || helpRequest.getStatus()==HelpRequestStatus.CANCELLED || helpRequest.getStatus()==HelpRequestStatus.EXPIRED) {
            throw new IllegalStateException("Live tracking is no longer active");
        }
        Optional<TrackingLocation> location=trackingLocationRepository.findTopByHelpRequestOrderByRecordedAtDesc(helpRequest);
        if(location.isEmpty()) {
            throw new ResourceNotFoundException("No tracking location available");
        }
        return toResponse(location.get());
    }
    private TrackingLocationResponse toResponse(TrackingLocation location) {
        boolean stale=Duration.between(location.getRecordedAt(),LocalDateTime.now()).getSeconds()>60;
        return new TrackingLocationResponse(location.getHelpRequest().getId(),location.getLatitude(),location.getLongitude(),location.getRecordedAt(),stale);
    }
    private HelpRequest getRequest(Long id) {
        return helpRequestRepository.findById(id).orElseThrow(()->new ResourceNotFoundException("Help request not found"));
    }
    private User getUser(String email) {
        return userRepository.findByEmail(email).orElseThrow(()->new ResourceNotFoundException("User not found"));
    }
}