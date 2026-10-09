package com.community.backend.service;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.context.ApplicationEventPublisher;
import com.community.backend.dto.CreateHelpRequest;
import com.community.backend.dto.AcceptHelpRequestRequest;
import com.community.backend.dto.HelpRequestResponse;
import com.community.backend.dto.LocationResponse;
import com.community.backend.dto.UpdateHelpRequest;
import com.community.backend.entity.HelpRequest;
import com.community.backend.entity.HelpRequestLocation;
import com.community.backend.entity.HelperProfile;
import com.community.backend.repository.HelperProfileRepository;
import com.community.backend.entity.IdempotencyRecord;
import com.community.backend.entity.HelpRequestStatus;
import com.community.backend.entity.User;
import com.community.backend.event.HelpRequestCreatedEvent;
import com.community.backend.exception.ResourceNotFoundException;
import com.community.backend.exception.UnauthorizedException;
import com.community.backend.repository.HelpRequestLocationRepository;
import com.community.backend.repository.IdempotencyRecordRepository;
import com.community.backend.repository.HelpRequestRepository;
import com.community.backend.repository.UserRepository;
@Service
public class HelpRequestService {
    private HelpRequestRepository helpRequestRepository;
    private HelpRequestLocationRepository helpRequestLocationRepository;
    private HelperProfileRepository helperProfileRepository;
    private UserRepository userRepository;
    private IdempotencyRecordRepository idempotencyRecordRepository;
    private AuditService auditService;
    private ApplicationEventPublisher eventPublisher;
    private SecureRandom secureRandom=new SecureRandom();
    private final long expiryHours;
    public HelpRequestService(HelpRequestRepository helpRequestRepository,HelpRequestLocationRepository helpRequestLocationRepository,HelperProfileRepository helperProfileRepository,UserRepository userRepository,IdempotencyRecordRepository idempotencyRecordRepository,AuditService auditService,ApplicationEventPublisher eventPublisher,@Value("${community.help-request.expiry-hours:24}") long expiryHours) {
        this.helpRequestRepository=helpRequestRepository;
        this.helpRequestLocationRepository=helpRequestLocationRepository;
        this.helperProfileRepository=helperProfileRepository;
        this.userRepository=userRepository;
        this.idempotencyRecordRepository=idempotencyRecordRepository;
        this.auditService=auditService;
        this.eventPublisher=eventPublisher;
        this.expiryHours=Math.max(1,expiryHours);
    }
    @Transactional
    public HelpRequestResponse createRequest(CreateHelpRequest request,String email) {
        return createRequest(request,email,null);
    }
    @Transactional
    public HelpRequestResponse createRequest(CreateHelpRequest request,String email,String idempotencyKey){
        String normalizedKey=idempotencyKey==null?null:idempotencyKey.trim();
        if(normalizedKey!=null && (normalizedKey.length()<8 || normalizedKey.length()>100)) throw new IllegalArgumentException("Idempotency-Key must contain between 8 and 100 characters");
        if(normalizedKey!=null) {
            Optional<IdempotencyRecord> existing=idempotencyRecordRepository.findByIdempotencyKeyAndUserEmailAndOperation(normalizedKey,email,"CREATE_HELP_REQUEST");
            if(existing.isPresent()) {
                HelpRequest previousRequest=helpRequestRepository.findById(existing.get().getResourceId()).orElseThrow(()->new ResourceNotFoundException("The original request for this idempotency key no longer exists"));
                return toResponse(previousRequest);
            }
        }
        Optional<User> user=userRepository.findByEmail(email);
        if(user.isEmpty()) throw new RuntimeException("User not found");
        if(request.getLatitude()==null || request.getLongitude()==null) {
            throw new IllegalArgumentException("Location is required");
        }
        if(request.getLatitude()<-90 || request.getLatitude()>90 || request.getLongitude()<-180 || request.getLongitude()>180) {
            throw new IllegalArgumentException("Invalid location");
        }
        User existingUser=user.get();
        if(existingUser.getVerificationStatus()!=com.community.backend.entity.VerificationStatus.VERIFIED) throw new UnauthorizedException("Account verification is required before creating a help request");
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
        helpRequest.setExpiresAt(now.plusHours(expiryHours));
        helpRequest=helpRequestRepository.save(helpRequest);
        HelpRequestLocation helpRequestLocation=new HelpRequestLocation();
        helpRequestLocation.setHelpRequest(helpRequest);
        helpRequestLocation.setExactLatitude(request.getLatitude());
        helpRequestLocation.setExactLongitude(request.getLongitude());
        double angle=secureRandom.nextDouble()*2*Math.PI;
        double distance=500;
        double latitudeOffset=distance/111320;
        double cosineLatitude=Math.cos(Math.toRadians(request.getLatitude()));
        double longitudeOffset=Math.abs(cosineLatitude)<0.000001?0:distance/(111320*cosineLatitude);
        double approximateLatitude=request.getLatitude()+latitudeOffset*Math.cos(angle);
        double approximateLongitude=request.getLongitude()+longitudeOffset*Math.sin(angle);
        approximateLatitude=Math.max(-90,Math.min(90,approximateLatitude));
        approximateLongitude=Math.max(-180,Math.min(180,approximateLongitude));
        helpRequestLocation.setApproximateLatitude(approximateLatitude);
        helpRequestLocation.setApproximateLongitude(approximateLongitude);
        helpRequestLocation.setPrivacyRadiusMeters((int)distance);
        helpRequestLocationRepository.save(helpRequestLocation);
        auditService.record(helpRequest,existingUser,"REQUEST_CREATED","OPEN");
        eventPublisher.publishEvent(new HelpRequestCreatedEvent(helpRequest));
        if(normalizedKey!=null) {
            IdempotencyRecord idempotencyRecord=new IdempotencyRecord();
            idempotencyRecord.setIdempotencyKey(normalizedKey);
            idempotencyRecord.setUserEmail(email);
            idempotencyRecord.setOperation("CREATE_HELP_REQUEST");
            idempotencyRecord.setResourceId(helpRequest.getId());
            idempotencyRecord.setCreatedAt(now);
            idempotencyRecordRepository.save(idempotencyRecord);
        }
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
        List<HelpRequest> helpRequests=helpRequestRepository.findAllByStatusOrderByCreatedAtDesc(HelpRequestStatus.OPEN);
        List<HelpRequestResponse> responses=new ArrayList<>();
        for(HelpRequest helpRequest:helpRequests) {
            if(helpRequest.getExpiresAt()!=null && !helpRequest.getExpiresAt().isAfter(LocalDateTime.now())) continue;
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
    public HelpRequestResponse acceptHelpRequest(Long id,String email,AcceptHelpRequestRequest acceptance) {
        Optional<HelpRequest> helpRequest=helpRequestRepository.findByIdForUpdate(id);
        if(helpRequest.isEmpty()) {
            throw new ResourceNotFoundException("Help request not found");
        }
        HelpRequest req=helpRequest.get();
        if(req.getStatus()==HelpRequestStatus.OPEN && req.getExpiresAt()!=null && !req.getExpiresAt().isAfter(LocalDateTime.now())) {
            req.setStatus(HelpRequestStatus.EXPIRED);
            req.setUpdatedAt(LocalDateTime.now());
            req=helpRequestRepository.save(req);
            auditService.record(req.getId(),"SYSTEM","REQUEST_EXPIRED","EXPIRED");
            return toResponse(req);
        }
        if(req.getStatus()==HelpRequestStatus.ACCEPTED && req.getHelper()!=null && req.getHelper().getEmail().equals(email)) {
            return toResponse(req);
        }
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
        if(helper.getVerificationStatus()!=com.community.backend.entity.VerificationStatus.VERIFIED) throw new UnauthorizedException("Account verification is required before accepting help requests");
        HelperProfile profile=helperProfileRepository.findByUser(helper).orElseThrow(()->new IllegalStateException("Configure a helper profile before accepting help requests"));
        if(!profile.isAvailable()) throw new IllegalStateException("Enable helper availability before accepting help requests");
        List<String> skills=java.util.Arrays.stream(profile.getSkills().split(",")).map(String::trim).filter(value->!value.isEmpty()).toList();
        if(!skills.contains("ALL") && !skills.contains(req.getCategory().name())) throw new UnauthorizedException("Your helper profile does not include this request category");
        Optional<HelpRequestLocation> requestLocation=helpRequestLocationRepository.findByHelpRequest(req);
        if(requestLocation.isEmpty()) throw new ResourceNotFoundException("Help request location not found");
        double distance=distanceKm(acceptance.getLatitude(),acceptance.getLongitude(),requestLocation.get().getApproximateLatitude(),requestLocation.get().getApproximateLongitude());
        if(distance>profile.getMaxRadiusKm()) throw new IllegalStateException("Help request is outside your configured service radius");
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
    @Transactional
    public HelpRequestResponse startJourney(Long id,String email) {
        Optional<HelpRequest> helpRequest=helpRequestRepository.findById(id);
        if(helpRequest.isEmpty()) {
            throw new ResourceNotFoundException("Help request not found");
        }
        HelpRequest req=helpRequest.get();
        if(req.getHelper()==null || !(req.getHelper().getEmail().equals(email))) {
            throw new UnauthorizedException("User is not the assigned helper");
        }
        if(req.getStatus()==HelpRequestStatus.EN_ROUTE) return toResponse(req);
        if(req.getStatus()!=HelpRequestStatus.ACCEPTED) {
            throw new IllegalStateException("Help request is not accepted for starting journey");
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
    @Transactional
    public HelpRequestResponse markArrived(Long id,String email) {
        Optional<HelpRequest> helpRequest=helpRequestRepository.findById(id);
        if(helpRequest.isEmpty()) {
            throw new ResourceNotFoundException("Help request not found");
        }
        HelpRequest req=helpRequest.get();
        if(req.getHelper()==null || !(req.getHelper().getEmail().equals(email))) {
            throw new UnauthorizedException("User is not the assigned helper");
        }
        if(req.getStatus()==HelpRequestStatus.ARRIVED) return toResponse(req);
        if(req.getStatus()!=HelpRequestStatus.EN_ROUTE) {
            throw new IllegalStateException("Help request is not in en route state");
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
    @Transactional
    public HelpRequestResponse startAssistance(Long id,String email) {
        Optional<HelpRequest> helpRequest=helpRequestRepository.findById(id);
        if(helpRequest.isEmpty()) {
            throw new ResourceNotFoundException("Help request not found");
        }
        HelpRequest req=helpRequest.get();
        boolean isCreator=req.getCreator().getEmail().equals(email);
        boolean isHelper=req.getHelper()!=null && req.getHelper().getEmail().equals(email);
        if(!isCreator && !isHelper) {
            throw new UnauthorizedException("User is not authorized to start assistance");
        }
        if(req.getStatus()==HelpRequestStatus.IN_PROGRESS) return toResponse(req);
        if(req.getStatus()!=HelpRequestStatus.ARRIVED) throw new IllegalStateException("Help request is not in arrived state");
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
    @Transactional
    public HelpRequestResponse requestCompletion(Long id,String email) {
        Optional<HelpRequest> helpRequest=helpRequestRepository.findById(id);
        if(helpRequest.isEmpty()) {
            throw new ResourceNotFoundException("Help request not found");
        }
        HelpRequest req=helpRequest.get();
        boolean isCreator=req.getCreator().getEmail().equals(email);
        boolean isHelper=req.getHelper()!=null && req.getHelper().getEmail().equals(email);
        if(!isCreator && !isHelper) {
            throw new UnauthorizedException("User is not authorized to request completion");
        }
        if(req.getStatus()==HelpRequestStatus.COMPLETION_PENDING) return toResponse(req);
        if(req.getStatus()!=HelpRequestStatus.IN_PROGRESS) throw new IllegalStateException("Help request is not in progress");
        req.setStatus(HelpRequestStatus.COMPLETION_PENDING);
        req.setCompletionRequestedBy(isHelper?req.getHelper():req.getCreator());
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
    @Transactional
    public HelpRequestResponse completeHelpRequest(Long id,String email) {
        Optional<HelpRequest> helpRequest=helpRequestRepository.findById(id);
        if(helpRequest.isEmpty()) {
            throw new ResourceNotFoundException("Help request not found");
        }
        HelpRequest req=helpRequest.get();
        boolean isCreator=req.getCreator().getEmail().equals(email);
        boolean isHelper=req.getHelper()!=null && req.getHelper().getEmail().equals(email);
        if(!isCreator && !isHelper) {
            throw new UnauthorizedException("User is not authorized to complete the request");
        }
        if(req.getStatus()==HelpRequestStatus.COMPLETED) return toResponse(req);
        if(req.getStatus()!=HelpRequestStatus.COMPLETION_PENDING) throw new IllegalStateException("Help request is not pending completion");
        if(req.getCompletionRequestedBy()!=null && req.getCompletionRequestedBy().getEmail().equals(email)) throw new IllegalStateException("The other participant must confirm completion");
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
            if(req.getStatus()==HelpRequestStatus.COMPLETED || req.getStatus()==HelpRequestStatus.CANCELLED || req.getStatus()==HelpRequestStatus.EXPIRED || req.getStatus()==HelpRequestStatus.DISPUTED) {
                throw new UnauthorizedException("Exact location access has ended for this request");
            }
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
    private double distanceKm(double lat1,double lon1,double lat2,double lon2) {
        double earthRadius=6371.0088;
        double dLat=Math.toRadians(lat2-lat1);
        double dLon=Math.toRadians(lon2-lon1);
        double a=Math.sin(dLat/2)*Math.sin(dLat/2)+Math.cos(Math.toRadians(lat1))*Math.cos(Math.toRadians(lat2))*Math.sin(dLon/2)*Math.sin(dLon/2);
        return earthRadius*2*Math.atan2(Math.sqrt(a),Math.sqrt(Math.max(0,1-a)));
    }
    private HelpRequestResponse toResponse(HelpRequest req) {
        HelpRequestResponse response=new HelpRequestResponse();
        response.setId(req.getId());
        response.setTitle(req.getTitle());
        response.setDescription(req.getDescription());
        response.setCategory(req.getCategory());
        response.setUrgency(req.getUrgency());
        response.setStatus(req.getStatus());
        return response;
    }
}
