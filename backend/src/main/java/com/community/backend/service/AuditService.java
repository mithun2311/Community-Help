package com.community.backend.service;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.community.backend.entity.AuditEvent;
import com.community.backend.entity.HelpRequest;
import com.community.backend.entity.User;
import com.community.backend.repository.AuditEventRepository;
import com.community.backend.repository.HelpRequestRepository;
@Service
public class AuditService {
    private final AuditEventRepository auditEventRepository;
    private final HelpRequestRepository helpRequestRepository;
    public AuditService(AuditEventRepository auditEventRepository,HelpRequestRepository helpRequestRepository) {
        this.auditEventRepository=auditEventRepository;
        this.helpRequestRepository=helpRequestRepository;
    }
    @Transactional
    public synchronized AuditEvent record(HelpRequest request,User actor,String eventType,String eventData) {
        return record(request.getId(),actor.getEmail(),eventType,eventData);
    }
    @Transactional
    public synchronized AuditEvent record(Long helpRequestId,String actorEmail,String eventType,String eventData) {
        if(helpRequestRepository.findByIdForUpdate(helpRequestId).isEmpty()) throw new IllegalStateException("Cannot audit a missing help request");
        Optional<AuditEvent> previous=auditEventRepository.findTopByHelpRequestIdOrderByIdDesc(helpRequestId);
        String previousHash=previous.map(AuditEvent::getEventHash).orElse("GENESIS");
        LocalDateTime now=LocalDateTime.now();
        String raw=buildPayload(helpRequestId,actorEmail,eventType,eventData,now,previousHash);
        AuditEvent event=new AuditEvent();
        event.setHelpRequestId(helpRequestId);
        event.setActorEmail(actorEmail);
        event.setEventType(eventType);
        event.setEventData(eventData);
        event.setCreatedAt(now);
        event.setPreviousHash(previousHash);
        event.setEventHash(hash(raw));
        return auditEventRepository.save(event);
    }
    @Transactional(readOnly=true)
    public boolean getRequestAuditVerification(HelpRequest request) {
        return verifyRequestAudit(request);
    }
    @Transactional(readOnly=true)
    public boolean verifyRequestAudit(HelpRequest request) {
        return verifyChain(request.getId());
    }
    @Transactional(readOnly=true)
    public boolean verifyChain(Long helpRequestId) {
        List<AuditEvent> events=auditEventRepository.findByHelpRequestIdOrderByIdAsc(helpRequestId);
        String previousHash="GENESIS";
        for(AuditEvent event:events) {
            if(!previousHash.equals(event.getPreviousHash())) return false;
            String expectedHash=hash(buildPayload(event.getHelpRequestId(),event.getActorEmail(),event.getEventType(),event.getEventData(),event.getCreatedAt(),previousHash));
            if(!expectedHash.equals(event.getEventHash())) return false;
            previousHash=event.getEventHash();
        }
        return true;
    }
    @Transactional(readOnly=true)
    public List<AuditEvent> getRequestAudit(HelpRequest request) {
        return getEvents(request.getId());
    }
    @Transactional(readOnly=true)
    public List<AuditEvent> getEvents(Long helpRequestId) {
        return auditEventRepository.findByHelpRequestIdOrderByIdAsc(helpRequestId);
    }
    private String buildPayload(Long requestId,String actorEmail,String eventType,String eventData,LocalDateTime createdAt,String previousHash) {
        return requestId+"|"+actorEmail+"|"+eventType+"|"+eventData+"|"+createdAt+"|"+previousHash;
    }
    private String hash(String value) {
        try {
            byte[] bytes=MessageDigest.getInstance("SHA-256").digest(value.getBytes(StandardCharsets.UTF_8));
            StringBuilder builder=new StringBuilder(bytes.length*2);
            for(byte b:bytes) builder.append(String.format("%02x",b));
            return builder.toString();
        } catch(Exception exception) {
            throw new IllegalStateException("Unable to create audit hash");
        }
    }
}
