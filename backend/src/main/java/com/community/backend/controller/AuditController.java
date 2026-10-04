package com.community.backend.controller;
import java.util.List;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.community.backend.entity.AuditEvent;
import com.community.backend.entity.HelpRequest;
import com.community.backend.exception.ResourceNotFoundException;
import com.community.backend.repository.HelpRequestRepository;
import com.community.backend.service.AuditService;
@RestController
@RequestMapping("/api/help-requests")
public class AuditController {
    private AuditService auditService;
    private HelpRequestRepository helpRequestRepository;
    public AuditController(AuditService auditService,HelpRequestRepository helpRequestRepository) {
        this.auditService=auditService;
        this.helpRequestRepository=helpRequestRepository;
    }
    @GetMapping("/{id}/audit")
    public List<AuditEvent> getAudit(@PathVariable Long id) {
        HelpRequest request=getRequest(id);
        validateParticipant(request);
        return auditService.getRequestAudit(request);
    }
    @GetMapping("/{id}/audit/verify")
    public boolean verifyAudit(@PathVariable Long id) {
        HelpRequest request=getRequest(id);
        validateParticipant(request);
        return auditService.verifyRequestAudit(request);
    }
    private HelpRequest getRequest(Long id) {
        return helpRequestRepository.findById(id).orElseThrow(()->new ResourceNotFoundException("Help request not found"));
    }
    private void validateParticipant(HelpRequest request) {
        Authentication authentication=SecurityContextHolder.getContext().getAuthentication();
        String email=authentication.getName();
        boolean requester=request.getCreator().getEmail().equals(email);
        boolean helper=request.getHelper()!=null && request.getHelper().getEmail().equals(email);
        if(!requester && !helper) {
            throw new org.springframework.security.access.AccessDeniedException("User is not authorized");
        }
    }
}