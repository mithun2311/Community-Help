package com.community.backend.service;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.community.backend.entity.HelpRequestStatus;
import com.community.backend.entity.SafetyCheckIn;
import com.community.backend.entity.SafetyCheckInStatus;
import com.community.backend.event.SafetyCheckInEscalatedEvent;
import com.community.backend.repository.SafetyCheckInRepository;
import com.community.backend.repository.TrustedContactRepository;
@Service
public class SafetyCheckInEscalationService {
    private final SafetyCheckInRepository checkInRepository;
    private final TrustedContactRepository trustedContactRepository;
    private final AuditService auditService;
    private final ApplicationEventPublisher eventPublisher;
    private final long timeoutMinutes;
    public SafetyCheckInEscalationService(SafetyCheckInRepository checkInRepository,TrustedContactRepository trustedContactRepository,AuditService auditService,ApplicationEventPublisher eventPublisher,@Value("${community.safety-checkin.timeout-minutes:5}") long timeoutMinutes) {
        this.checkInRepository=checkInRepository; this.trustedContactRepository=trustedContactRepository; this.auditService=auditService; this.eventPublisher=eventPublisher; this.timeoutMinutes=Math.max(1,timeoutMinutes);
    }
    @Scheduled(fixedDelayString="${community.safety-checkin.scan-ms:60000}")
    @Transactional
    public void escalateOverdueCheckIns() {
        LocalDateTime now=LocalDateTime.now();
        List<SafetyCheckIn> overdue=checkInRepository.findByStatusAndCreatedAtBefore(SafetyCheckInStatus.PENDING,now.minusMinutes(timeoutMinutes));
        for(SafetyCheckIn checkIn:overdue) {
            boolean active=checkIn.getHelpRequest().getStatus()==HelpRequestStatus.IN_PROGRESS;
            checkIn.setStatus(active?SafetyCheckInStatus.ESCALATED:SafetyCheckInStatus.MISSED);
            checkIn.setRespondedAt(now);
            checkInRepository.save(checkIn);
            auditService.record(checkIn.getHelpRequest(),checkIn.getUser(),active?"SAFETY_CHECKIN_ESCALATED":"SAFETY_CHECKIN_MISSED",checkIn.getStatus().name());
            if(active) eventPublisher.publishEvent(new SafetyCheckInEscalatedEvent(checkIn,trustedContactRepository.findByUser(checkIn.getUser())));
        }
    }
}
