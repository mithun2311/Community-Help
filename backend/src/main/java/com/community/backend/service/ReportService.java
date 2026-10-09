package com.community.backend.service;
import java.time.LocalDateTime;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.community.backend.dto.ReportRequest;
import com.community.backend.entity.ChatMessage;
import com.community.backend.entity.HelpRequest;
import com.community.backend.entity.Report;
import com.community.backend.entity.ReportStatus;
import com.community.backend.entity.ReportType;
import com.community.backend.entity.User;
import com.community.backend.exception.ResourceNotFoundException;
import com.community.backend.exception.UnauthorizedException;
import com.community.backend.repository.ChatMessageRepository;
import com.community.backend.repository.HelpRequestRepository;
import com.community.backend.repository.ReportRepository;
import com.community.backend.repository.UserRepository;
@Service
public class ReportService {
    private ReportRepository reportRepository;
    private UserRepository userRepository;
    private HelpRequestRepository helpRequestRepository;
    private ChatMessageRepository chatMessageRepository;
    public ReportService(ReportRepository reportRepository,UserRepository userRepository,HelpRequestRepository helpRequestRepository,ChatMessageRepository chatMessageRepository) {
        this.reportRepository=reportRepository;
        this.userRepository=userRepository;
        this.helpRequestRepository=helpRequestRepository;
        this.chatMessageRepository=chatMessageRepository;
    }
    @Transactional
    public Report reportUser(Long userId,ReportRequest request,String email) {
        User reporter=getUser(email);
        User reported=getUserById(userId);
        if(reporter.getId().equals(reported.getId())) {
            throw new IllegalArgumentException("You cannot report yourself");
        }
        Report report=new Report();
        report.setReportedBy(reporter);
        report.setReportedUser(reported);
        report.setType(ReportType.USER);
        report.setStatus(ReportStatus.OPEN);
        report.setReason(validateReason(request.getReason()));
        report.setCreatedAt(LocalDateTime.now());
        return reportRepository.save(report);
    }
    @Transactional
    public Report reportRequest(Long requestId,ReportRequest request,String email) {
        User reporter=getUser(email);
        HelpRequest helpRequest=getRequest(requestId);
        validateParticipant(helpRequest,email);
        Report report=new Report();
        report.setReportedBy(reporter);
        report.setHelpRequest(helpRequest);
        report.setType(ReportType.REQUEST);
        report.setStatus(ReportStatus.OPEN);
        report.setReason(validateReason(request.getReason()));
        report.setCreatedAt(LocalDateTime.now());
        return reportRepository.save(report);
    }
    @Transactional
    public Report reportMessage(Long messageId,ReportRequest request,String email) {
        User reporter=getUser(email);
        ChatMessage message=chatMessageRepository.findById(messageId).orElseThrow(()->new ResourceNotFoundException("Message not found"));
        validateParticipant(message.getChat().getHelpRequest(),email);
        if(message.getSender().getEmail().equals(email)) throw new IllegalArgumentException("You cannot report your own message");
        Report report=new Report();
        report.setReportedBy(reporter);
        report.setChatMessage(message);
        report.setReportedUser(message.getSender());
        report.setType(ReportType.MESSAGE);
        report.setStatus(ReportStatus.OPEN);
        report.setReason(validateReason(request.getReason()));
        report.setCreatedAt(LocalDateTime.now());
        return reportRepository.save(report);
    }
    private String validateReason(String reason) {
        if(reason==null || reason.trim().isEmpty()) {
            throw new IllegalArgumentException("Report reason is required");
        }
        if(reason.length()>1000) {
            throw new IllegalArgumentException("Report reason is too long");
        }
        return reason.trim();
    }
    private User getUser(String email) {
        return userRepository.findByEmail(email).orElseThrow(()->new ResourceNotFoundException("User not found"));
    }
    private User getUserById(Long id) {
        return userRepository.findById(id).orElseThrow(()->new ResourceNotFoundException("User not found"));
    }
    private HelpRequest getRequest(Long id) {
        return helpRequestRepository.findById(id).orElseThrow(()->new ResourceNotFoundException("Help request not found"));
    }
    private void validateParticipant(HelpRequest request,String email) {
        boolean requester=request.getCreator().getEmail().equals(email);
        boolean helper=request.getHelper()!=null && request.getHelper().getEmail().equals(email);
        if(!requester && !helper) {
            throw new UnauthorizedException("User is not authorized");
        }
    }
}