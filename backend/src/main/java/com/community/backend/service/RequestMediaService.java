package com.community.backend.service;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.community.backend.dto.AddHelpRequestImageRequest;
import com.community.backend.dto.HelpRequestImageResponse;
import com.community.backend.dto.UpdateHelpRequestTagsRequest;
import com.community.backend.entity.HelpRequest;
import com.community.backend.entity.HelpRequestImage;
import com.community.backend.entity.HelpRequestStatus;
import com.community.backend.entity.HelpRequestTag;
import com.community.backend.entity.User;
import com.community.backend.exception.ResourceNotFoundException;
import com.community.backend.exception.UnauthorizedException;
import com.community.backend.repository.HelpRequestImageRepository;
import com.community.backend.repository.HelpRequestRepository;
import com.community.backend.repository.HelpRequestTagRepository;
import com.community.backend.repository.UserRepository;
@Service
public class RequestMediaService {
    private final HelpRequestRepository requestRepository;
    private final HelpRequestImageRepository imageRepository;
    private final HelpRequestTagRepository tagRepository;
    private final UserRepository userRepository;
    public RequestMediaService(HelpRequestRepository requestRepository,HelpRequestImageRepository imageRepository,HelpRequestTagRepository tagRepository,UserRepository userRepository) {
        this.requestRepository=requestRepository; this.imageRepository=imageRepository; this.tagRepository=tagRepository; this.userRepository=userRepository;
    }
    @Transactional
    public HelpRequestImageResponse addImage(Long requestId,AddHelpRequestImageRequest body,String email) {
        HelpRequest request=getRequest(requestId);
        validateOwnerCanEdit(request,email);
        String url=validateImageUrl(body.getImageUrl());
        if(imageRepository.countByHelpRequest(request)>=5) throw new IllegalStateException("A help request can have at most 5 images");
        if(imageRepository.existsByHelpRequestAndImageUrl(request,url)) throw new IllegalStateException("This image is already attached to the request");
        HelpRequestImage image=new HelpRequestImage(); image.setHelpRequest(request); image.setImageUrl(url); image.setCreatedAt(LocalDateTime.now());
        image=imageRepository.save(image);
        return toResponse(image);
    }
    public List<HelpRequestImageResponse> getImages(Long requestId,String email) {
        HelpRequest request=getRequest(requestId); validateParticipant(request,email);
        return imageRepository.findByHelpRequestOrderByCreatedAtAsc(request).stream().map(this::toResponse).collect(Collectors.toList());
    }
    @Transactional
    public List<String> replaceTags(Long requestId,UpdateHelpRequestTagsRequest body,String email) {
        HelpRequest request=getRequest(requestId); validateOwnerCanEdit(request,email);
        List<String> tags=body.getTags().stream().map(String::trim).map(value->value.toUpperCase(Locale.ROOT)).distinct().toList();
        if(tags.stream().anyMatch(String::isBlank)) throw new IllegalArgumentException("Tags cannot be blank");
        if(tags.size()>10) throw new IllegalArgumentException("A request can have at most 10 unique tags");
        if(tags.stream().anyMatch(value->value.length()>40)) throw new IllegalArgumentException("Each tag must be 40 characters or fewer");
        tagRepository.deleteByHelpRequest(request);
        for(String tag:tags) { HelpRequestTag entity=new HelpRequestTag(); entity.setHelpRequest(request); entity.setTag(tag); tagRepository.save(entity); }
        return tags;
    }
    public List<String> getTags(Long requestId,String email) {
        HelpRequest request=getRequest(requestId); validateParticipant(request,email);
        return tagRepository.findByHelpRequestOrderByTagAsc(request).stream().map(HelpRequestTag::getTag).collect(Collectors.toList());
    }
    private String validateImageUrl(String value) {
        try {
            URI uri=URI.create(value.trim());
            if(!"https".equalsIgnoreCase(uri.getScheme()) || uri.getHost()==null || uri.getUserInfo()!=null) throw new IllegalArgumentException("Image URL must be an absolute HTTPS URL");
            return uri.toASCIIString();
        } catch(IllegalArgumentException exception) { throw new IllegalArgumentException("Image URL must be an absolute HTTPS URL"); }
    }
    private void validateOwnerCanEdit(HelpRequest request,String email) {
        if(!request.getCreator().getEmail().equals(email)) throw new UnauthorizedException("Only the requester can manage request images and tags");
        if(request.getStatus()!=HelpRequestStatus.OPEN) throw new IllegalStateException("Images and tags can only be changed while the request is open");
    }
    private void validateParticipant(HelpRequest request,String email) {
        boolean requester=request.getCreator().getEmail().equals(email);
        boolean helper=request.getHelper()!=null && request.getHelper().getEmail().equals(email);
        if(!requester && !helper) throw new UnauthorizedException("Only request participants can view images and tags");
    }
    private HelpRequest getRequest(Long id) { return requestRepository.findById(id).orElseThrow(()->new ResourceNotFoundException("Help request not found")); }
    private HelpRequestImageResponse toResponse(HelpRequestImage image) { return new HelpRequestImageResponse(image.getId(),image.getHelpRequest().getId(),image.getImageUrl(),image.getCreatedAt()); }
}
