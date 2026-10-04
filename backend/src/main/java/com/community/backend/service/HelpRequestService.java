package com.community.backend.service;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.community.backend.dto.CreateHelpRequest;
import com.community.backend.dto.HelpRequestResponse;
import com.community.backend.dto.LocationResponse;
import com.community.backend.dto.UpdateHelpRequest;
import com.community.backend.entity.HelpRequest;
import com.community.backend.entity.HelpRequestLocation;
import com.community.backend.entity.HelpRequestStatus;
import com.community.backend.entity.User;
import com.community.backend.exception.ResourceNotFoundException;
import com.community.backend.exception.UnauthorizedException;
import com.community.backend.repository.HelpRequestLocationRepository;
import com.community.backend.repository.HelpRequestRepository;
import com.community.backend.repository.UserRepository;
@Service
public class HelpRequestService {
    private HelpRequestRepository helpRequestRepository;
    private HelpRequestLocationRepository helpRequestLocationRepository;
    private UserRepository userRepository;
    private AuditService auditService;
    private SecureRandom secureRandom=new SecureRandom();
    public HelpRequestService(HelpRequestRepository helpRequestRepository,HelpRequestLocationRepository helpRequestLocationRepository,UserRepository userRepository,AuditService auditService) {
        this.helpRequestRepository=helpRequestRepository;
        this.helpRequestLocationRepository=helpRequestLocationRepository;
        this.userRepository=userRepository;
        this.auditService=auditService;
    }
    @Transactional
    public HelpRequestResponse createRequest(CreateHelpRequest request,String email){
        Optional<User> user=userRepository.findByEmail(email);
        if(user.isEmpty()) throw new RuntimeException("User not found");
        if(request.getLatitude()==null || request.getLongitude()==null) {
            throw new IllegalArgumentException("Location is required");
        }
        if(request.getLatitude()<-90 || request.getLatitude()>90 || request.getLongitude()<-180 || request.getLongitude()>180) {
            throw new IllegalArgumentException("Invalid location");
        }
        User existingUser=user.get();
        HelpRequest helpRequest=new HelpRequest();
        helpRequest.setTitle(request.getTitle());
        helpRequest.setDescription(request.getDescription());
        helpRequest.setCategory(request.getCategory());
        helpRequest.setUrgency(request.getUrgency());
        helpRequest.setCreator(existingUser);
        helpRequest.setStatus(HelpRequestStatus.OPEN);
        LocalDateTime now=LocalDateTime.now();
        helpRequest.setCreatedAt(now);
        helpRequest.setUpdatedAt(now);
        helpRequest=helpRequestRepository.save(helpRequest);
        HelpRequestLocation helpRequestLocation=new HelpRequestLocation();
        helpRequestLocation.setHelpRequest(helpRequest);
        helpRequestLocation.setExactLatitude(request.getLatitude());
        helpRequestLocation.setExactLongitude(request.getLongitude());
        double angle=secureRandom.nextDouble()*2*Math.PI;
        double distance=500+secureRandom.nextDouble()*500;
        double latitudeOffset=distance/111320;
        double longitudeOffset=distance/(111320*Math.cos(Math.toRadians(request.getLatitude())));
        double approximateLatitude=request.getLatitude()+latitudeOffset*Math.cos(angle);
        double approximateLongitude=request.getLongitude()+longitudeOffset*Math.sin(angle);
        approximateLatitude=Math.max(-90,Math.min(90,approximateLatitude));
        approximateLongitude=Math.max(-180,Math.min(180,approximateLongitude));
        helpRequestLocation.setApproximateLatitude(approximateLatitude);
        helpRequestLocation.setApproximateLongitude(approximateLongitude);
        helpRequestLocation.setPrivacyRadiusMeters((int)distance);
        helpRequestLocationRepository.save(helpRequestLocation);
        auditService.record(helpRequest,existingUser,"REQUEST_CREATED","OPEN");
        HelpRequestResponse response=new HelpRequestResponse();
        response.setId(helpRequest.getId());
        response.setTitle(helpRequest.getTitle());
        response.setDescription(helpRequest.getDescription());
        response.setCategory(helpRequest.getCategory());
        response.setUrgency(helpRequest.getUrgency());
        response.setStatus(helpRequest.getStatus());
        return response;
    }
    public List<HelpRequestResponse> checkHelpRequests() {
        List<HelpRequest> helpRequests=helpRequestRepository.findAllByOrderByCreatedAtDesc();
        List<HelpRequestResponse> responses=new ArrayList<>();
        for(HelpRequest helpRequest:helpRequests) {
            HelpRequestResponse response=new HelpRequestResponse();
            response.setId(helpRequest.getId());
            response.setTitle(helpRequest.getTitle());
            response.setDescription(helpRequest.getDescription());
            response.setCategory(helpRequest.getCategory());
            response.setUrgency(helpRequest.getUrgency());
            response.setStatus(helpRequest.getStatus());
            responses.add(response);
        }
        return responses;
    }
    public HelpRequestResponse getHelpRequestById(Long id) {
        Optional<HelpRequest> helpRequest=helpRequestRepository.findById(id);
        if(helpRequest.isEmpty()) {
            throw new ResourceNotFoundException("Help request not found");
        }
        HelpRequest existingHelpRequest=helpRequest.get();
        HelpRequestResponse response=new HelpRequestResponse();
        response.setId(existingHelpRequest.getId());
        response.setTitle(existingHelpRequest.getTitle());
        response.setDescription(existingHelpRequest.getDescription());
        response.setCategory(existingHelpRequest.getCategory());
        response.setUrgency(existingHelpRequest.getUrgency());
        response.setStatus(existingHelpRequest.getStatus());
        return response;
    }
    @Transactional
    public HelpRequestResponse updateHelpRequest(Long id,UpdateHelpRequest request,String email) {
        Optional<HelpRequest> helpRequest=helpRequestRepository.findById(id);
        if(helpRequest.isEmpty()) {
            throw new ResourceNotFoundException("Help request not found");
        }
        HelpRequest req=helpRequest.get();
        if(req.getStatus()!=HelpRequestStatus.OPEN) {
            throw new IllegalStateException("Only open help requests can be updated");
        }
        if(!(req.getCreator().getEmail().equals(email))) {
            throw new UnauthorizedException("User is not authorized to update the request");
        }
        req.setTitle(request.getTitle());
        req.setDescription(request.getDescription());
        req.setUrgency(request.getUrgency());
        req.setUpdatedAt(LocalDateTime.now());
        req=helpRequestRepository.save(req);
        auditService.record(req,req.getCreator(),"REQUEST_UPDATED","OPEN");
        HelpRequestResponse response=new HelpRequestResponse();
        response.setId(req.getId());
        response.setTitle(req.getTitle());
        response.setDescription(req.getDescription());
        response.setCategory(req.getCategory());
        response.setUrgency(req.getUrgency());
        response.setStatus(req.getStatus());
        return response;
    }
    @Transactional
    public HelpRequestResponse acceptHelpRequest(Long id,String email) {
        Optional<HelpRequest> helpRequest=helpRequestRepository.findById(id);
        if(helpRequest.isEmpty()) {
            throw new ResourceNotFoundException("Help request not found");
        }
        HelpRequest req=helpRequest.get();
        if(req.getStatus()!=HelpRequestStatus.OPEN) {
            throw new IllegalStateException("Help request is not open for acceptance");
        }
        if(req.getCreator().getEmail().equals(email)) {
            throw new UnauthorizedException("User cannot accept their own help request");
        }
        Optional<User> user=userRepository.findByEmail(email);
        if(user.isEmpty()) {
            throw new RuntimeException("User not found");
        }
        User helper=user.get();
        req.setHelper(helper);
        req.setStatus(HelpRequestStatus.ACCEPTED);
        req.setUpdatedAt(LocalDateTime.now());
        req=helpRequestRepository.save(req);
        auditService.record(req,helper,"REQUEST_ACCEPTED","ACCEPTED");
        HelpRequestResponse response=new HelpRequestResponse();
        response.setId(req.getId());
        response.setTitle(req.getTitle());
        response.setDescription(req.getDescription());
        response.setCategory(req.getCategory());
        response.setUrgency(req.getUrgency());
        response.setStatus(req.getStatus());
        return response;
    }
    @Transactional
    public HelpRequestResponse startJourney(Long id,String email) {
        Optional<HelpRequest> helpRequest=helpRequestRepository.findById(id);
        if(helpRequest.isEmpty()) {
            throw new ResourceNotFoundException("Help request not found");
        }
        HelpRequest req=helpRequest.get();
        if(req.getStatus()!=HelpRequestStatus.ACCEPTED) {
            throw new IllegalStateException("Help request is not accepted for starting journey");
        }
        if(req.getHelper()==null || !(req.getHelper().getEmail().equals(email))) {
            throw new UnauthorizedException("User is not the assigned helper");
        }
        req.setStatus(HelpRequestStatus.EN_ROUTE);
        req.setUpdatedAt(LocalDateTime.now());
        req=helpRequestRepository.save(req);
        auditService.record(req,req.getHelper(),"EN_ROUTE_STARTED","EN_ROUTE");
        HelpRequestResponse response=new HelpRequestResponse();
        response.setId(req.getId());
        response.setTitle(req.getTitle());
        response.setDescription(req.getDescription());
        response.setCategory(req.getCategory());
        response.setUrgency(req.getUrgency());
        response.setStatus(req.getStatus());
        return response;
    }
    @Transactional
    public HelpRequestResponse markArrived(Long id,String email) {
        Optional<HelpRequest> helpRequest=helpRequestRepository.findById(id);
        if(helpRequest.isEmpty()) {
            throw new ResourceNotFoundException("Help request not found");
        }
        HelpRequest req=helpRequest.get();
        if(req.getStatus()!=HelpRequestStatus.EN_ROUTE) {
            throw new IllegalStateException("Help request is not in en route state");
        }
        if(req.getHelper()==null || !(req.getHelper().getEmail().equals(email))) {
            throw new UnauthorizedException("User is not the assigned helper");
        }
        req.setStatus(HelpRequestStatus.ARRIVED);
        req.setUpdatedAt(LocalDateTime.now());
        req=helpRequestRepository.save(req);
        auditService.record(req,req.getHelper(),"ARRIVED","ARRIVED");
        HelpRequestResponse response=new HelpRequestResponse();
        response.setId(req.getId());
        response.setTitle(req.getTitle());
        response.setDescription(req.getDescription());
        response.setCategory(req.getCategory());
        response.setUrgency(req.getUrgency());
        response.setStatus(req.getStatus());
        return response;
    }
    @Transactional
    public HelpRequestResponse startAssistance(Long id,String email) {
        Optional<HelpRequest> helpRequest=helpRequestRepository.findById(id);
        if(helpRequest.isEmpty()) {
            throw new ResourceNotFoundException("Help request not found");
        }
        HelpRequest req=helpRequest.get();
        if(req.getStatus()!=HelpRequestStatus.ARRIVED) {
            throw new IllegalStateException("Help request is not in arrived state");
        }
        boolean isCreator=req.getCreator().getEmail().equals(email);
        boolean isHelper=req.getHelper()!=null && req.getHelper().getEmail().equals(email);
        if(!isCreator && !isHelper) {
            throw new UnauthorizedException("User is not authorized to start assistance");
        }
        req.setStatus(HelpRequestStatus.IN_PROGRESS);
        req.setUpdatedAt(LocalDateTime.now());
        req=helpRequestRepository.save(req);
        User actor=isHelper ? req.getHelper() : req.getCreator();
        auditService.record(req,actor,"ASSISTANCE_STARTED","IN_PROGRESS");
        HelpRequestResponse response=new HelpRequestResponse();
        response.setId(req.getId());
        response.setTitle(req.getTitle());
        response.setDescription(req.getDescription());
        response.setCategory(req.getCategory());
        response.setUrgency(req.getUrgency());
        response.setStatus(req.getStatus());
        return response;
    }
    @Transactional
    public HelpRequestResponse requestCompletion(Long id,String email) {
        Optional<HelpRequest> helpRequest=helpRequestRepository.findById(id);
        if(helpRequest.isEmpty()) {
            throw new ResourceNotFoundException("Help request not found");
        }
        HelpRequest req=helpRequest.get();
        if(req.getStatus()!=HelpRequestStatus.IN_PROGRESS) {
            throw new IllegalStateException("Help request is not in progress");
        }
        boolean isCreator=req.getCreator().getEmail().equals(email);
        boolean isHelper=req.getHelper()!=null && req.getHelper().getEmail().equals(email);
        if(!isCreator && !isHelper) {
            throw new UnauthorizedException("User is not authorized to request completion");
        }
        req.setStatus(HelpRequestStatus.COMPLETION_PENDING);
        req.setUpdatedAt(LocalDateTime.now());
        req=helpRequestRepository.save(req);
        User actor=isHelper ? req.getHelper() : req.getCreator();
        auditService.record(req,actor,"COMPLETION_REQUESTED","COMPLETION_PENDING");
        HelpRequestResponse response=new HelpRequestResponse();
        response.setId(req.getId());
        response.setTitle(req.getTitle());
        response.setDescription(req.getDescription());
        response.setCategory(req.getCategory());
        response.setUrgency(req.getUrgency());
        response.setStatus(req.getStatus());
        return response;
    }
    @Transactional
    public HelpRequestResponse completeHelpRequest(Long id,String email) {
        Optional<HelpRequest> helpRequest=helpRequestRepository.findById(id);
        if(helpRequest.isEmpty()) {
            throw new ResourceNotFoundException("Help request not found");
        }
        HelpRequest req=helpRequest.get();
        if(req.getStatus()!=HelpRequestStatus.COMPLETION_PENDING) {
            throw new IllegalStateException("Help request is not pending completion");
        }
        boolean isCreator=req.getCreator().getEmail().equals(email);
        boolean isHelper=req.getHelper()!=null && req.getHelper().getEmail().equals(email);
        if(!isCreator && !isHelper) {
            throw new UnauthorizedException("User is not authorized to complete the request");
        }
        req.setStatus(HelpRequestStatus.COMPLETED);
        req.setUpdatedAt(LocalDateTime.now());
        req=helpRequestRepository.save(req);
        User actor=isHelper ? req.getHelper() : req.getCreator();
        auditService.record(req,actor,"REQUEST_COMPLETED","COMPLETED");
        HelpRequestResponse response=new HelpRequestResponse();
        response.setId(req.getId());
        response.setTitle(req.getTitle());
        response.setDescription(req.getDescription());
        response.setCategory(req.getCategory());
        response.setUrgency(req.getUrgency());
        response.setStatus(req.getStatus());
        return response;
    }
    @Transactional
    public HelpRequestResponse cancelHelpRequest(Long id,String email) {
        Optional<HelpRequest> helpRequest=helpRequestRepository.findById(id);
        if(helpRequest.isEmpty()) {
            throw new ResourceNotFoundException("Help request not found");
        }
        HelpRequest req=helpRequest.get();
        if(req.getStatus()==HelpRequestStatus.COMPLETED || req.getStatus()==HelpRequestStatus.CANCELLED || req.getStatus()==HelpRequestStatus.EXPIRED || req.getStatus()==HelpRequestStatus.DISPUTED || req.getStatus()==HelpRequestStatus.COMPLETION_PENDING) {
            throw new IllegalStateException("Help request cannot be cancelled in its current state");
        }
        boolean isCreator=req.getCreator().getEmail().equals(email);
        boolean isHelper=req.getHelper()!=null && req.getHelper().getEmail().equals(email);
        if(!isCreator && !isHelper) {
            throw new UnauthorizedException("User is not authorized to cancel the request");
        }
        User actor=isHelper ? req.getHelper() : req.getCreator();
        String eventType;
        if(isHelper && !isCreator) {
            req.setHelper(null);
            req.setStatus(HelpRequestStatus.OPEN);
            eventType="HELPER_WITHDREW";
        } else {
            req.setStatus(HelpRequestStatus.CANCELLED);
            eventType="REQUEST_CANCELLED";
        }
        req.setUpdatedAt(LocalDateTime.now());
        req=helpRequestRepository.save(req);
        auditService.record(req,actor,eventType,req.getStatus().name());
        HelpRequestResponse response=new HelpRequestResponse();
        response.setId(req.getId());
        response.setTitle(req.getTitle());
        response.setDescription(req.getDescription());
        response.setCategory(req.getCategory());
        response.setUrgency(req.getUrgency());
        response.setStatus(req.getStatus());
        return response;
    }
    public LocationResponse getHelpRequestLocation(Long id,String email) {
        Optional<HelpRequest> helpRequest=helpRequestRepository.findById(id);
        if(helpRequest.isEmpty()) {
            throw new ResourceNotFoundException("Help request not found");
        }
        HelpRequest req=helpRequest.get();
        Optional<HelpRequestLocation> location=helpRequestLocationRepository.findByHelpRequest(req);
        if(location.isEmpty()) {
            throw new ResourceNotFoundException("Location not found");
        }
        HelpRequestLocation existingLocation=location.get();
        boolean isCreator=req.getCreator().getEmail().equals(email);
        boolean isHelper=req.getHelper()!=null && req.getHelper().getEmail().equals(email);
        LocationResponse response=new LocationResponse();
        response.setRequestId(req.getId());
        if(isCreator) {
            response.setAccessLevel("EXACT");
            response.setLatitude(existingLocation.getExactLatitude());
            response.setLongitude(existingLocation.getExactLongitude());
            response.setPrivacyRadiusMeters(0);
            return response;
        }
        if(isHelper) {
            response.setAccessLevel("EXACT");
            response.setLatitude(existingLocation.getExactLatitude());
            response.setLongitude(existingLocation.getExactLongitude());
            response.setPrivacyRadiusMeters(0);
            return response;
        }
        if(req.getStatus()==HelpRequestStatus.OPEN) {
            response.setAccessLevel("APPROXIMATE");
            response.setLatitude(existingLocation.getApproximateLatitude());
            response.setLongitude(existingLocation.getApproximateLongitude());
            response.setPrivacyRadiusMeters(existingLocation.getPrivacyRadiusMeters());
            return response;
        }
        throw new UnauthorizedException("User is not authorized to access this location");
    }
}