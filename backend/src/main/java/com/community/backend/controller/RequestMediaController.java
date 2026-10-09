package com.community.backend.controller;
import java.util.List;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.community.backend.dto.AddHelpRequestImageRequest;
import com.community.backend.dto.HelpRequestImageResponse;
import com.community.backend.dto.UpdateHelpRequestTagsRequest;
import com.community.backend.service.RequestMediaService;
import jakarta.validation.Valid;
@RestController
@RequestMapping("/api/help-requests/{id}")
public class RequestMediaController {
    private final RequestMediaService mediaService;
    public RequestMediaController(RequestMediaService mediaService) { this.mediaService=mediaService; }
    @GetMapping("/images")
    public List<HelpRequestImageResponse> getImages(@PathVariable Long id,Authentication authentication) { return mediaService.getImages(id,authentication.getName()); }
    @PostMapping("/images")
    public HelpRequestImageResponse addImage(@PathVariable Long id,@Valid @RequestBody AddHelpRequestImageRequest request,Authentication authentication) { return mediaService.addImage(id,request,authentication.getName()); }
    @GetMapping("/tags")
    public List<String> getTags(@PathVariable Long id,Authentication authentication) { return mediaService.getTags(id,authentication.getName()); }
    @PutMapping("/tags")
    public List<String> replaceTags(@PathVariable Long id,@Valid @RequestBody UpdateHelpRequestTagsRequest request,Authentication authentication) { return mediaService.replaceTags(id,request,authentication.getName()); }
}
