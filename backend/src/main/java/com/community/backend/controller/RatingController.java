package com.community.backend.controller;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.community.backend.dto.RatingRequest;
import com.community.backend.dto.RatingResponse;
import com.community.backend.entity.Rating;
import com.community.backend.service.RatingService;
import jakarta.validation.Valid;
@RestController
@RequestMapping("/api/help-requests")
public class RatingController {
    private final RatingService ratingService;
    public RatingController(RatingService ratingService) { this.ratingService=ratingService; }
    @PostMapping("/{id}/rating")
    public RatingResponse createRating(@PathVariable Long id,@Valid @RequestBody RatingRequest request,Authentication authentication) { return RatingResponse.from(ratingService.createRating(id,request,authentication.getName())); }
}
