package com.community.backend.controller;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.community.backend.dto.ReportRequest;
import com.community.backend.entity.Report;
import com.community.backend.service.ReportService;
@RestController
@RequestMapping("/api/reports")
public class ReportController {
    private ReportService reportService;
    public ReportController(ReportService reportService) {
        this.reportService=reportService;
    }
    @PostMapping("/users/{id}")
    public Report reportUser(@PathVariable Long id,@RequestBody ReportRequest request) {
        return reportService.reportUser(id,request,getEmail());
    }
    @PostMapping("/requests/{id}")
    public Report reportRequest(@PathVariable Long id,@RequestBody ReportRequest request) {
        return reportService.reportRequest(id,request,getEmail());
    }
    @PostMapping("/messages/{id}")
    public Report reportMessage(@PathVariable Long id,@RequestBody ReportRequest request) {
        return reportService.reportMessage(id,request,getEmail());
    }
    private String getEmail() {
        Authentication authentication=SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }
}