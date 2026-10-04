package com.community.backend.service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import com.community.backend.entity.HelpRequest;
import com.community.backend.entity.HelpRequestStatus;
import com.community.backend.entity.SOSIncident;
import com.community.backend.entity.SafetyCheckIn;
import com.community.backend.entity.SafetyCheckInStatus;
import com.community.backend.entity.TrustedContact;
import com.community.backend.entity.User;
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
    public SafetyService(HelpRequestRepository helpRequestRepository,UserRepository userRepository,SafetyCheckInRepository safetyCheckInRepository,TrustedContactRepository trustedContactRepository,SOSIncidentRepository sosIncidentRepository) {
        this.helpRequestRepository=helpRequestRepository;
        this.userRepository=userRepository;
        this.safetyCheckInRepository=safetyCheckInRepository;
        this.trustedContactRepository=trustedContactRepository;
        this.sosIncidentRepository=sosIncidentRepository;
    }
    public SafetyCheckIn createCheckIn(Long id,String email) {
        HelpRequest request=getRequest(id);
        User user=getUser(email);
        validateParticipant(request,email);
        if(request.getStatus()!=HelpRequestStatus.IN_PROGRESS) {
            throw new IllegalStateException("Safety check-in is only available during active assistance");
        }
        SafetyCheckIn checkIn=new SafetyCheckIn();
        checkIn.setHelpRequest(request);
        checkIn.setUser(user);
        checkIn.setStatus(SafetyCheckInStatus.PENDING);
        checkIn.setCreatedAt(LocalDateTime.now());
        return safetyCheckInRepository.save(checkIn);
    }
    public SafetyCheckIn markSafe(Long checkInId,String email) {
        SafetyCheckIn checkIn=safetyCheckInRepository.findById(checkInId).orElseThrow(()->new ResourceNotFoundException("Safety check-in not found"));
        if(!checkIn.getUser().getEmail().equals(email)) {
            throw new UnauthorizedException("User is not authorized to respond to this check-in");
        }
        if(checkIn.getStatus()!=SafetyCheckInStatus.PENDING) {
            throw new IllegalStateException("Safety check-in is no longer pending");
        }
        checkIn.setStatus(SafetyCheckInStatus.SAFE);
        checkIn.setRespondedAt(LocalDateTime.now());
        return safetyCheckInRepository.save(checkIn);
    }
    public List<TrustedContact> getTrustedContacts(String email) {
        return trustedContactRepository.findByUser(getUser(email));
    }
    public TrustedContact addTrustedContact(String email,String name,String phone) {
        User user=getUser(email);
        TrustedContact contact=new TrustedContact();
        contact.setUser(user);
        contact.setName(name);
        contact.setPhone(phone);
        return trustedContactRepository.save(contact);
    }
    public void deleteTrustedContact(Long id,String email) {
        TrustedContact contact=trustedContactRepository.findById(id).orElseThrow(()->new ResourceNotFoundException("Trusted contact not found"));
        if(!contact.getUser().getEmail().equals(email)) {
            throw new UnauthorizedException("User is not authorized");
        }
        trustedContactRepository.delete(contact);
    }
    public SOSIncident triggerSOS(Long id,String email) {
        HelpRequest request=getRequest(id);
        User user=getUser(email);
        validateParticipant(request,email);
        if(request.getStatus()==HelpRequestStatus.COMPLETED || request.getStatus()==HelpRequestStatus.CANCELLED) {
            throw new IllegalStateException("SOS cannot be triggered for a completed or cancelled request");
        }
        SOSIncident incident=new SOSIncident();
        incident.setHelpRequest(request);
        incident.setTriggeredBy(user);
        incident.setStatus("ACTIVE");
        incident.setCreatedAt(LocalDateTime.now());
        return sosIncidentRepository.save(incident);
    }
    public SOSIncident resolveSOS(Long id,String email) {
        SOSIncident incident=sosIncidentRepository.findById(id).orElseThrow(()->new ResourceNotFoundException("SOS incident not found"));
        validateParticipant(incident.getHelpRequest(),email);
        if(!"ACTIVE".equals(incident.getStatus())) {
            throw new IllegalStateException("SOS is not active");
        }
        incident.setStatus("RESOLVED");
        incident.setResolvedAt(LocalDateTime.now());
        return sosIncidentRepository.save(incident);
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