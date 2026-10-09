package com.community.backend.controller;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.community.backend.dto.ReportRequest;
import com.community.backend.dto.ReportResponse;
import com.community.backend.service.ReportService;
import jakarta.validation.Valid;
@RestController
@RequestMapping("/api/reports")
public class ReportController {
    private final ReportService reportService;
    public ReportController(ReportService reportService) { this.reportService=reportService; }
    @PostMapping("/users/{id}")
    public ReportResponse reportUser(@PathVariable Long id,@Valid @RequestBody ReportRequest request,Authentication authentication) { return ReportResponse.from(reportService.reportUser(id,request,authentication.getName())); }
    @PostMapping("/requests/{id}")
    public ReportResponse reportRequest(@PathVariable Long id,@Valid @RequestBody ReportRequest request,Authentication authentication) { return ReportResponse.from(reportService.reportRequest(id,request,authentication.getName())); }
    @PostMapping("/messages/{id}")
    public ReportResponse reportMessage(@PathVariable Long id,@Valid @RequestBody ReportRequest request,Authentication authentication) { return ReportResponse.from(reportService.reportMessage(id,request,authentication.getName())); }
}
