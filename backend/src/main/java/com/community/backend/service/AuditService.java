package com.community.backend.service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.community.backend.entity.AuditEvent;
import com.community.backend.repository.AuditEventRepository;

@Service
public class AuditService {

    private final AuditEventRepository auditEventRepository;

    public AuditService(AuditEventRepository auditEventRepository) {
        this.auditEventRepository = auditEventRepository;
    }

    public AuditEvent record(Long helpRequestId, String actorEmail, String eventType, String eventData) {
        Optional<AuditEvent> previous = auditEventRepository.findTopByOrderByIdDesc();
        String previousHash = previous.map(AuditEvent::getEventHash).orElse("GENESIS");

        LocalDateTime now = LocalDateTime.now();

        String raw = helpRequestId + "|" + actorEmail + "|" + eventType + "|" + eventData + "|" + now + "|" + previousHash;

        AuditEvent event = new AuditEvent();
        event.setHelpRequestId(helpRequestId);
        event.setActorEmail(actorEmail);
        event.setEventType(eventType);
        event.setEventData(eventData);
        event.setCreatedAt(now);
        event.setPreviousHash(previousHash);
        event.setEventHash(hash(raw));

        return auditEventRepository.save(event);
    }

    public boolean verifyChain(Long helpRequestId) {
        List<AuditEvent> events = auditEventRepository.findByHelpRequestIdOrderByIdAsc(helpRequestId);

        String previousHash = "GENESIS";

        for (AuditEvent event : events) {
            String raw = event.getHelpRequestId() + "|" + event.getActorEmail() + "|" + event.getEventType() + "|" + event.getEventData() + "|" + event.getCreatedAt() + "|" + previousHash;

            String expectedHash = hash(raw);

            if (!previousHash.equals(event.getPreviousHash())) {
                return false;
            }

            if (!expectedHash.equals(event.getEventHash())) {
                return false;
            }

            previousHash = event.getEventHash();
        }

        return true;
    }

    public List<AuditEvent> getEvents(Long helpRequestId) {
        return auditEventRepository.findByHelpRequestIdOrderByIdAsc(helpRequestId);
    }

    private String hash(String value) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] bytes = digest.digest(value.getBytes(StandardCharsets.UTF_8));

            StringBuilder builder = new StringBuilder();

            for (byte b : bytes) {
                builder.append(String.format("%02x", b));
            }

            return builder.toString();
        } catch (Exception e) {
            throw new IllegalStateException("Unable to create audit hash");
        }
    }
}