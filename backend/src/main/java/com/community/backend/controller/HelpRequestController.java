package com.community.backend.controller;
import jakarta.validation.Valid;
import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.community.backend.dto.CreateHelpRequest;
import com.community.backend.dto.AcceptHelpRequestRequest;
import com.community.backend.dto.HelpRequestResponse;
import com.community.backend.dto.HelpRequestMatchResponse;
import com.community.backend.entity.HelpCategory;
import com.community.backend.service.MatchingService;
import com.community.backend.dto.LocationResponse;
import com.community.backend.dto.UpdateHelpRequest;
import com.community.backend.service.HelpRequestService;
@RestController
@RequestMapping("/api/help-requests")
public class HelpRequestController {
    private HelpRequestService helpRequestService;
    private MatchingService matchingService;
    public HelpRequestController(HelpRequestService helpRequestService,MatchingService matchingService) {
        this.helpRequestService=helpRequestService;
        this.matchingService=matchingService;
    }
    @PostMapping
    public HelpRequestResponse createHelpRequest(@Valid @RequestBody CreateHelpRequest request,@RequestHeader(value="Idempotency-Key",required=false) String idempotencyKey) {
        Authentication authentication=SecurityContextHolder.getContext().getAuthentication();
        String email=authentication.getName();
        return helpRequestService.createRequest(request,email,idempotencyKey);
    }
    @GetMapping
    public List<HelpRequestResponse> checkHelpRequests() {
        return helpRequestService.checkHelpRequests();
    }
    @GetMapping("/matches")
    public List<HelpRequestMatchResponse> getMatches(@RequestParam double latitude,@RequestParam double longitude,@RequestParam(defaultValue="25") double radiusKm,@RequestParam(required=false) HelpCategory category) {
        Authentication authentication=SecurityContextHolder.getContext().getAuthentication();
        return matchingService.findMatches(authentication.getName(),latitude,longitude,radiusKm,category);
    }
    @GetMapping("/{id}")
    public HelpRequestResponse getHelpRequestById(@PathVariable Long id) {
        return helpRequestService.getHelpRequestById(id);
    }
    @PutMapping("/{id}")
    public HelpRequestResponse updateHelpRequest(@PathVariable Long id,@Valid @RequestBody UpdateHelpRequest request) {
        Authentication authentication=SecurityContextHolder.getContext().getAuthentication();
        String email=authentication.getName();
        return helpRequestService.updateHelpRequest(id,request,email);
    }
    @PutMapping("/{id}/accept")
    public HelpRequestResponse acceptHelpRequest(@PathVariable Long id,@Valid @RequestBody AcceptHelpRequestRequest request) {
        Authentication authentication=SecurityContextHolder.getContext().getAuthentication();
        String email=authentication.getName();
        return helpRequestService.acceptHelpRequest(id,email,request);
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