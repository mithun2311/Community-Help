package com.community.backend.service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.context.ApplicationEventPublisher;
import com.community.backend.dto.SOSResponse;
import com.community.backend.dto.SafetyCheckInResponse;
import com.community.backend.dto.TrustedContactResponse;
import com.community.backend.entity.HelpRequest;
import com.community.backend.entity.HelpRequestStatus;
import com.community.backend.entity.SOSIncident;
import com.community.backend.entity.SafetyCheckIn;
import com.community.backend.entity.SafetyCheckInStatus;
import com.community.backend.entity.TrustedContact;
import com.community.backend.entity.User;
import com.community.backend.event.SosTriggeredEvent;
import com.community.backend.exception.ResourceNotFoundException;
import com.community.backend.exception.UnauthorizedException;
import com.community.backend.repository.HelpRequestRepository;
import com.community.backend.repository.SOSIncidentRepository;
import com.community.backend.repository.SafetyCheckInRepository;
import com.community.backend.repository.TrustedContactRepository;
import com.community.backend.repository.UserRepository;
@Service
public class SafetyService {
    private HelpRequestRepository helpRequestRepository;
    private UserRepository userRepository;
    private SafetyCheckInRepository safetyCheckInRepository;
    private TrustedContactRepository trustedContactRepository;
    private SOSIncidentRepository sosIncidentRepository;
    private AuditService auditService;
    private ApplicationEventPublisher eventPublisher;
    public SafetyService(HelpRequestRepository helpRequestRepository,UserRepository userRepository,SafetyCheckInRepository safetyCheckInRepository,TrustedContactRepository trustedContactRepository,SOSIncidentRepository sosIncidentRepository,AuditService auditService,ApplicationEventPublisher eventPublisher) {
        this.helpRequestRepository=helpRequestRepository;
        this.userRepository=userRepository;
        this.safetyCheckInRepository=safetyCheckInRepository;
        this.trustedContactRepository=trustedContactRepository;
        this.sosIncidentRepository=sosIncidentRepository;
        this.auditService=auditService;
        this.eventPublisher=eventPublisher;
    }
    @Transactional
    public SafetyCheckInResponse createCheckIn(Long id,String email) {
        HelpRequest request=getRequest(id);
        User user=getUser(email);
        validateParticipant(request,email);
        if(request.getStatus()!=HelpRequestStatus.IN_PROGRESS) {
            throw new IllegalStateException("Safety check-in is only available during active assistance");
        }
        safetyCheckInRepository.findTopByHelpRequestAndUserOrderByCreatedAtDesc(request,user).ifPresent(existing -> {
            if(existing.getStatus()==SafetyCheckInStatus.PENDING) {
                throw new IllegalStateException("A safety check-in is already pending");
            }
        });
        SafetyCheckIn checkIn=new SafetyCheckIn();
        checkIn.setHelpRequest(request);
        checkIn.setUser(user);
        checkIn.setStatus(SafetyCheckInStatus.PENDING);
        checkIn.setCreatedAt(LocalDateTime.now());
        checkIn=safetyCheckInRepository.save(checkIn);
        auditService.record(request,user,"SAFETY_CHECKIN_CREATED","PENDING");
        return toCheckInResponse(checkIn);
    }
    @Transactional
    public SafetyCheckInResponse markSafe(Long checkInId,String email) {
        SafetyCheckIn checkIn=safetyCheckInRepository.findById(checkInId).orElseThrow(()->new ResourceNotFoundException("Safety check-in not found"));
        if(!checkIn.getUser().getEmail().equals(email)) {
            throw new UnauthorizedException("User is not authorized to respond to this check-in");
        }
        if(checkIn.getStatus()!=SafetyCheckInStatus.PENDING) {
            throw new IllegalStateException("Safety check-in is no longer pending");
        }
        if(checkIn.getHelpRequest().getStatus()!=HelpRequestStatus.IN_PROGRESS) {
            throw new IllegalStateException("Safety check-in is no longer active");
        }
        checkIn.setStatus(SafetyCheckInStatus.SAFE);
        checkIn.setRespondedAt(LocalDateTime.now());
        checkIn=safetyCheckInRepository.save(checkIn);
        auditService.record(checkIn.getHelpRequest(),checkIn.getUser(),"SAFETY_CHECKIN_SAFE","SAFE");
        return toCheckInResponse(checkIn);
    }
    public List<TrustedContactResponse> getTrustedContacts(String email) {
        User user=getUser(email);
        return trustedContactRepository.findByUser(user).stream().map(this::toTrustedContactResponse).collect(Collectors.toList());
    }
    @Transactional
    public TrustedContactResponse addTrustedContact(String email,String name,String phone,String contactEmail) {
        if(name==null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Trusted contact name is required");
        }
        if(phone==null || phone.trim().isEmpty()) {
            throw new IllegalArgumentException("Trusted contact phone is required");
        }
        User user=getUser(email);
        TrustedContact contact=new TrustedContact();
        contact.setUser(user);
        contact.setName(name.trim());
        contact.setPhone(phone.trim());
        contact.setEmail(contactEmail==null || contactEmail.isBlank()?null:contactEmail.trim().toLowerCase(java.util.Locale.ROOT));
        contact=trustedContactRepository.save(contact);
        return toTrustedContactResponse(contact);
    }
    @Transactional
    public void deleteTrustedContact(Long id,String email) {
        TrustedContact contact=trustedContactRepository.findById(id).orElseThrow(()->new ResourceNotFoundException("Trusted contact not found"));
        if(!contact.getUser().getEmail().equals(email)) {
            throw new UnauthorizedException("User is not authorized");
        }
        trustedContactRepository.delete(contact);
    }
    @Transactional
    public SOSResponse triggerSOS(Long id,String email) {
        HelpRequest request=getRequest(id);
        User user=getUser(email);
        validateParticipant(request,email);
        if(request.getStatus()==HelpRequestStatus.COMPLETED || request.getStatus()==HelpRequestStatus.CANCELLED || request.getStatus()==HelpRequestStatus.EXPIRED || request.getStatus()==HelpRequestStatus.DISPUTED) {
            throw new IllegalStateException("SOS cannot be triggered for a completed or cancelled request");
        }
        sosIncidentRepository.findTopByHelpRequestOrderByCreatedAtDesc(request).ifPresent(existing -> {
            if("ACTIVE".equals(existing.getStatus())) {
                throw new IllegalStateException("An active SOS already exists for this request");
            }
        });
        SOSIncident incident=new SOSIncident();
        incident.setHelpRequest(request);
        incident.setTriggeredBy(user);
        incident.setStatus("ACTIVE");
        incident.setCreatedAt(LocalDateTime.now());
        incident=sosIncidentRepository.save(incident);
        auditService.record(request,user,"SOS_TRIGGERED","ACTIVE");
        eventPublisher.publishEvent(new SosTriggeredEvent(incident,trustedContactRepository.findByUser(user)));
        return toSOSResponse(incident);
    }
    @Transactional
    public SOSResponse resolveSOS(Long id,String email) {
        SOSIncident incident=sosIncidentRepository.findById(id).orElseThrow(()->new ResourceNotFoundException("SOS incident not found"));
        validateParticipant(incident.getHelpRequest(),email);
        if(!"ACTIVE".equals(incident.getStatus())) {
            throw new IllegalStateException("SOS is not active");
        }
        incident.setStatus("RESOLVED");
        incident.setResolvedAt(LocalDateTime.now());
        incident=sosIncidentRepository.save(incident);
        User user=getUser(email);
        auditService.record(incident.getHelpRequest(),user,"SOS_RESOLVED","RESOLVED");
        return toSOSResponse(incident);
    }
    private SafetyCheckInResponse toCheckInResponse(SafetyCheckIn checkIn) {
        return new SafetyCheckInResponse(checkIn.getId(),checkIn.getHelpRequest().getId(),checkIn.getStatus(),checkIn.getCreatedAt(),checkIn.getRespondedAt());
    }
    private TrustedContactResponse toTrustedContactResponse(TrustedContact contact) {
        return new TrustedContactResponse(contact.getId(),contact.getName(),contact.getPhone(),contact.getEmail());
    }
    private SOSResponse toSOSResponse(SOSIncident incident) {
        return new SOSResponse(incident.getId(),incident.getHelpRequest().getId(),incident.getStatus(),incident.getCreatedAt(),incident.getResolvedAt());
    }
    private HelpRequest getRequest(Long id) {
        return helpRequestRepository.findById(id).orElseThrow(()->new ResourceNotFoundException("Help request not found"));
    }
    private User getUser(String email) {
        return userRepository.findByEmail(email).orElseThrow(()->new ResourceNotFoundException("User not found"));
    }
    private void validateParticipant(HelpRequest request,String email) {
        boolean creator=request.getCreator().getEmail().equals(email);
        boolean helper=request.getHelper()!=null && request.getHelper().getEmail().equals(email);
        if(!creator && !helper) {
            throw new UnauthorizedException("User is not authorized for this request");
        }
    }
}