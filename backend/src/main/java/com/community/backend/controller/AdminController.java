package com.community.backend.controller;
import java.util.List;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.community.backend.dto.AdminReportResponse;
import com.community.backend.dto.AdminUserResponse;
import com.community.backend.dto.UpdateReportStatusRequest;
import com.community.backend.entity.ReportStatus;
import com.community.backend.entity.VerificationStatus;
import com.community.backend.service.AdminService;
import jakarta.validation.Valid;
@RestController
@RequestMapping("/api/admin")
public class AdminController {
    private final AdminService adminService;
    public AdminController(AdminService adminService) { this.adminService=adminService; }
    @GetMapping("/users")
    public List<AdminUserResponse> listUsers(Authentication authentication) { return adminService.listUsers(authentication.getName()); }
    @PutMapping("/users/{id}/verification")
    public AdminUserResponse updateVerification(@PathVariable Long id,@RequestParam VerificationStatus status,Authentication authentication) { return adminService.updateVerification(id,status,authentication.getName()); }
    @GetMapping("/reports")
    public List<AdminReportResponse> listReports(@RequestParam(required=false) ReportStatus status,Authentication authentication) { return adminService.listReports(status,authentication.getName()); }
    @PutMapping("/reports/{id}/status")
    public AdminReportResponse updateReportStatus(@PathVariable Long id,@Valid @RequestBody UpdateReportStatusRequest request,Authentication authentication) { return adminService.updateReportStatus(id,request.getStatus(),authentication.getName()); }
}
