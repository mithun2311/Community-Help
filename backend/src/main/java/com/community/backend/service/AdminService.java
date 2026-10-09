package com.community.backend.service;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.community.backend.dto.AdminReportResponse;
import com.community.backend.dto.AdminUserResponse;
import com.community.backend.entity.Report;
import com.community.backend.entity.ReportStatus;
import com.community.backend.entity.User;
import com.community.backend.entity.UserRole;
import com.community.backend.entity.VerificationStatus;
import com.community.backend.exception.ResourceNotFoundException;
import com.community.backend.exception.UnauthorizedException;
import com.community.backend.repository.ReportRepository;
import com.community.backend.repository.UserRepository;
@Service
public class AdminService {
    private final UserRepository userRepository;
    private final ReportRepository reportRepository;
    private final AuditService auditService;
    public AdminService(UserRepository userRepository,ReportRepository reportRepository,AuditService auditService) {
        this.userRepository=userRepository;
        this.reportRepository=reportRepository;
        this.auditService=auditService;
    }
    public List<AdminUserResponse> listUsers(String actorEmail) {
        requireAdmin(actorEmail);
        return userRepository.findAll().stream().map(user->new AdminUserResponse(user.getId(),user.getName(),user.getEmail(),user.getRole(),user.getAccountType(),user.getOrganizationName(),user.getVerificationStatus(),user.getVerificationNote(),user.getVerificationRequestedAt())).collect(Collectors.toList());
    }
    public List<AdminReportResponse> listReports(ReportStatus status,String actorEmail) {
        requireAdmin(actorEmail);
        List<Report> reports=status==null?reportRepository.findAllByOrderByCreatedAtDesc():reportRepository.findByStatusOrderByCreatedAtDesc(status);
        return reports.stream().map(this::toReportResponse).collect(Collectors.toList());
    }
    @Transactional
    public AdminReportResponse updateReportStatus(Long reportId,ReportStatus status,String actorEmail) {
        User admin=requireAdmin(actorEmail);
        if(status==null) throw new IllegalArgumentException("Report status is required");
        Report report=reportRepository.findById(reportId).orElseThrow(()->new ResourceNotFoundException("Report not found"));
        report.setStatus(status);
        report=reportRepository.save(report);
        if(report.getHelpRequest()!=null) auditService.record(report.getHelpRequest(),admin,"REPORT_STATUS_UPDATED",status.name());
        return toReportResponse(report);
    }
    @Transactional
    public AdminUserResponse updateVerification(Long userId,VerificationStatus status,String actorEmail) {
        User admin=requireAdmin(actorEmail);
        if(status==null) throw new IllegalArgumentException("Verification status is required");
        if(status==VerificationStatus.PENDING || status==VerificationStatus.UNVERIFIED) throw new IllegalArgumentException("An administrator must set verification to VERIFIED or REJECTED");
        User user=userRepository.findById(userId).orElseThrow(()->new ResourceNotFoundException("User not found"));
        user.setVerificationStatus(status);
        user=userRepository.save(user);
        return new AdminUserResponse(user.getId(),user.getName(),user.getEmail(),user.getRole(),user.getAccountType(),user.getOrganizationName(),user.getVerificationStatus(),user.getVerificationNote(),user.getVerificationRequestedAt());
    }
    private User requireAdmin(String email) {
        User user=userRepository.findByEmail(email.toLowerCase(Locale.ROOT)).orElseThrow(()->new ResourceNotFoundException("User not found"));
        if(user.getRole()!=UserRole.ADMIN) throw new UnauthorizedException("Administrator access is required");
        return user;
    }
    private AdminReportResponse toReportResponse(Report report) {
        User reporter=report.getReportedBy();
        User reported=report.getReportedUser();
        return new AdminReportResponse(report.getId(),reporter==null?null:reporter.getId(),reporter==null?null:reporter.getEmail(),reported==null?null:reported.getId(),reported==null?null:reported.getEmail(),report.getHelpRequest()==null?null:report.getHelpRequest().getId(),report.getChatMessage()==null?null:report.getChatMessage().getId(),report.getType(),report.getStatus(),report.getReason(),report.getCreatedAt());
    }
}
