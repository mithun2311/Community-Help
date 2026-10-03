package com.community.backend.service;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.community.backend.dto.CreateHelpRequest;
import com.community.backend.dto.HelpRequestResponse;
import com.community.backend.dto.UpdateHelpRequest;
import com.community.backend.entity.HelpRequest;
import com.community.backend.entity.HelpRequestStatus;
import com.community.backend.entity.User;
import com.community.backend.exception.ResourceNotFoundException;
import com.community.backend.exception.UnauthorizedException;
import com.community.backend.repository.HelpRequestRepository;
import com.community.backend.repository.UserRepository;
@Service
public class HelpRequestService {
    private HelpRequestRepository helpRequestRepository;
    private UserRepository userRepository;
    public HelpRequestService(HelpRequestRepository helpRequestRepository, UserRepository userRepository) {
        this.helpRequestRepository=helpRequestRepository;
        this.userRepository=userRepository;
    }
    public HelpRequestResponse createRequest(CreateHelpRequest request, String email){
        Optional<User> user=userRepository.findByEmail(email);
        if(user.isEmpty()) throw new RuntimeException("User not found");
        User existingUser=user.get();
        HelpRequest helpRequest=new HelpRequest();
        helpRequest.setTitle(request.getTitle());
        helpRequest.setDescription(request.getDescription());
        helpRequest.setCategory(request.getCategory());
        helpRequest.setUrgency(request.getUrgency());
        helpRequest.setCreator(existingUser);
        helpRequest.setStatus(HelpRequestStatus.OPEN);
        LocalDateTime now=LocalDateTime.now();
        helpRequest.setCreatedAt(now);
        helpRequest.setUpdatedAt(now);
        helpRequest=helpRequestRepository.save(helpRequest);
        HelpRequestResponse response=new HelpRequestResponse();
        response.setId(helpRequest.getId());
        response.setTitle(helpRequest.getTitle());
        response.setDescription(helpRequest.getDescription());
        response.setCategory(helpRequest.getCategory());
        response.setUrgency(helpRequest.getUrgency());
        response.setStatus(helpRequest.getStatus());
        return response;
    }
    public List<HelpRequestResponse> checkHelpRequests() {
        List<HelpRequest> helpRequests=helpRequestRepository.findAllByOrderByCreatedAtDesc();
        List<HelpRequestResponse> responses=new ArrayList<>();
        for(HelpRequest helpRequest:helpRequests) {
            HelpRequestResponse response=new HelpRequestResponse();
            response.setId(helpRequest.getId());
            response.setTitle(helpRequest.getTitle());
            response.setDescription(helpRequest.getDescription());
            response.setCategory(helpRequest.getCategory());
            response.setUrgency(helpRequest.getUrgency());
            response.setStatus(helpRequest.getStatus());
            responses.add(response);
        }
        return responses;
    }
    public HelpRequestResponse getHelpRequestById(Long id) {
        Optional<HelpRequest> helpRequest=helpRequestRepository.findById(id);
        if(helpRequest.isEmpty()) {
            throw new ResourceNotFoundException("Help request not found");
        }
        HelpRequest existingHelpRequest=helpRequest.get();
        HelpRequestResponse response=new HelpRequestResponse();
        response.setId(existingHelpRequest.getId());
        response.setTitle(existingHelpRequest.getTitle());
        response.setDescription(existingHelpRequest.getDescription());
        response.setCategory(existingHelpRequest.getCategory());
        response.setUrgency(existingHelpRequest.getUrgency());
        response.setStatus(existingHelpRequest.getStatus());
        return response;
    }
    public HelpRequestResponse updateHelpRequest(Long id, UpdateHelpRequest request, String email) {
        Optional<HelpRequest> helpRequest=helpRequestRepository.findById(id);
        if(helpRequest.isEmpty()) {
            throw new ResourceNotFoundException("Help request not found");
        }
        HelpRequest req=helpRequest.get();
        if(!(req.getCreator().getEmail().equals(email))) {
            throw new UnauthorizedException("User is not authorized to update the request");
        }
        req.setTitle(request.getTitle());
        req.setDescription(request.getDescription());
        req.setUrgency(request.getUrgency());
        req.setUpdatedAt(LocalDateTime.now());
        req=helpRequestRepository.save(req);
        HelpRequestResponse response=new HelpRequestResponse();
        response.setId(req.getId());
        response.setTitle(req.getTitle());
        response.setDescription(req.getDescription());
        response.setCategory(req.getCategory());
        response.setUrgency(req.getUrgency());
        response.setStatus(req.getStatus());
        return response;
    }
    public HelpRequestResponse acceptHelpRequest(Long id, String email) {
        Optional<HelpRequest> helpRequest=helpRequestRepository.findById(id);
        if(helpRequest.isEmpty()) {
            throw new ResourceNotFoundException("Help request not found");
        }
        HelpRequest req=helpRequest.get();
        if(req.getStatus()!=HelpRequestStatus.OPEN) {
            throw new IllegalStateException("Help request is not open for acceptance");
        }
        Optional<User> user=userRepository.findByEmail(email);
        if(user.isEmpty()) {
            throw new RuntimeException("User not found");
        }
        User helper=user.get();
        req.setHelper(helper);
        req.setStatus(HelpRequestStatus.ACCEPTED);
        req.setUpdatedAt(LocalDateTime.now());
        req=helpRequestRepository.save(req);
        HelpRequestResponse response=new HelpRequestResponse();
        response.setId(req.getId());
        response.setTitle(req.getTitle());
        response.setDescription(req.getDescription());
        response.setCategory(req.getCategory());
        response.setUrgency(req.getUrgency());
        response.setStatus(req.getStatus());
        return response;
    }
    public HelpRequestResponse completeHelpRequest(Long id, String email) {
        Optional<HelpRequest> helpRequest=helpRequestRepository.findById(id);
        if(helpRequest.isEmpty()) {
            throw new ResourceNotFoundException("Help request not found");
        }
        HelpRequest req=helpRequest.get();
        if(req.getStatus()!=HelpRequestStatus.ACCEPTED) {
            throw new IllegalStateException("Help request is not accepted for completion");
        }
        boolean isCreator=req.getCreator().getEmail().equals(email);
        boolean isHelper=req.getHelper()!=null && req.getHelper().getEmail().equals(email);
        if(!isCreator && !isHelper) {
            throw new UnauthorizedException("User is not authorized to complete the request");
        }
        req.setStatus(HelpRequestStatus.COMPLETED);
        req.setUpdatedAt(LocalDateTime.now());
        req=helpRequestRepository.save(req);
        HelpRequestResponse response=new HelpRequestResponse();
        response.setId(req.getId());
        response.setTitle(req.getTitle());
        response.setDescription(req.getDescription());
        response.setCategory(req.getCategory());
        response.setUrgency(req.getUrgency());
        response.setStatus(req.getStatus());
        return response;
    }
}