package com.community.backend.service;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.community.backend.entity.HelpRequest;
import com.community.backend.entity.HelpRequestStatus;
import com.community.backend.repository.HelpRequestRepository;
@Service
public class HelpRequestExpiryService {
    private final HelpRequestRepository helpRequestRepository;
    private final AuditService auditService;
    public HelpRequestExpiryService(HelpRequestRepository helpRequestRepository,AuditService auditService) {
        this.helpRequestRepository=helpRequestRepository;
        this.auditService=auditService;
    }
    @Scheduled(fixedDelayString="${community.help-request.expiry-scan-ms:60000}")
    @Transactional
    public void expireOpenRequests() {
        LocalDateTime now=LocalDateTime.now();
        List<HelpRequest> expired=helpRequestRepository.findAllByStatusAndExpiresAtBefore(HelpRequestStatus.OPEN,now);
        for(HelpRequest request:expired) {
            request.setStatus(HelpRequestStatus.EXPIRED);
            request.setUpdatedAt(now);
            auditService.record(request.getId(),"SYSTEM","REQUEST_EXPIRED","EXPIRED");
        }
    }
}
