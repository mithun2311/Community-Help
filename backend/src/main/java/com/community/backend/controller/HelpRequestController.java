package com.community.backend.controller;
import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.community.backend.dto.CreateHelpRequest;
import com.community.backend.dto.HelpRequestResponse;
import com.community.backend.dto.LocationResponse;
import com.community.backend.dto.UpdateHelpRequest;
import com.community.backend.service.HelpRequestService;
@RestController
@RequestMapping("/api/help-requests")
public class HelpRequestController {
    private HelpRequestService helpRequestService;
    public HelpRequestController(HelpRequestService helpRequestService) {
        this.helpRequestService=helpRequestService;
    }
    @PostMapping
    public HelpRequestResponse createHelpRequest(@RequestBody CreateHelpRequest request) {
        Authentication authentication=SecurityContextHolder.getContext().getAuthentication();
        String email=authentication.getName();
        return helpRequestService.createRequest(request,email);
    }
    @GetMapping
    public List<HelpRequestResponse> checkHelpRequests() {
        return helpRequestService.checkHelpRequests();
    }
    @GetMapping("/{id}")
    public HelpRequestResponse getHelpRequestById(@PathVariable Long id) {
        return helpRequestService.getHelpRequestById(id);
    }
    @PutMapping("/{id}")
    public HelpRequestResponse updateHelpRequest(@PathVariable Long id,@RequestBody UpdateHelpRequest request) {
        Authentication authentication=SecurityContextHolder.getContext().getAuthentication();
        String email=authentication.getName();
        return helpRequestService.updateHelpRequest(id,request,email);
    }
    @PutMapping("/{id}/accept")
    public HelpRequestResponse acceptHelpRequest(@PathVariable Long id) {
        Authentication authentication=SecurityContextHolder.getContext().getAuthentication();
        String email=authentication.getName();
        return helpRequestService.acceptHelpRequest(id,email);
    }
    @PutMapping("/{id}/start-journey")
    public HelpRequestResponse startJourney(@PathVariable Long id) {
        Authentication authentication=SecurityContextHolder.getContext().getAuthentication();
        String email=authentication.getName();
        return helpRequestService.startJourney(id,email);
    }
    @PutMapping("/{id}/arrived")
    public HelpRequestResponse markArrived(@PathVariable Long id) {
        Authentication authentication=SecurityContextHolder.getContext().getAuthentication();
        String email=authentication.getName();
        return helpRequestService.markArrived(id,email);
    }
    @PutMapping("/{id}/start-assistance")
    public HelpRequestResponse startAssistance(@PathVariable Long id) {
        Authentication authentication=SecurityContextHolder.getContext().getAuthentication();
        String email=authentication.getName();
        return helpRequestService.startAssistance(id,email);
    }
    @PutMapping("/{id}/request-completion")
    public HelpRequestResponse requestCompletion(@PathVariable Long id) {
        Authentication authentication=SecurityContextHolder.getContext().getAuthentication();
        String email=authentication.getName();
        return helpRequestService.requestCompletion(id,email);
    }
    @PutMapping("/{id}/complete")
    public HelpRequestResponse completeHelpRequest(@PathVariable Long id) {
        Authentication authentication=SecurityContextHolder.getContext().getAuthentication();
        String email=authentication.getName();
        return helpRequestService.completeHelpRequest(id,email);
    }
    @PutMapping("/{id}/cancel")
    public HelpRequestResponse cancelHelpRequest(@PathVariable Long id) {
        Authentication authentication=SecurityContextHolder.getContext().getAuthentication();
        String email=authentication.getName();
        return helpRequestService.cancelHelpRequest(id,email);
    }
    @GetMapping("/{id}/location")
    public LocationResponse getHelpRequestLocation(@PathVariable Long id) {
        Authentication authentication=SecurityContextHolder.getContext().getAuthentication();
        String email=authentication.getName();
        return helpRequestService.getHelpRequestLocation(id,email);
    }
}