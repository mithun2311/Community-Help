package com.community.backend.controller;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.community.backend.dto.RatingRequest;
import com.community.backend.entity.Rating;
import com.community.backend.service.RatingService;
@RestController
@RequestMapping("/api/help-requests")
public class RatingController {
    private RatingService ratingService;
    public RatingController(RatingService ratingService) {
        this.ratingService=ratingService;
    }
    @PostMapping("/{id}/rating")
    public Rating createRating(@PathVariable Long id,@RequestBody RatingRequest request) {
        Authentication authentication=SecurityContextHolder.getContext().getAuthentication();
        return ratingService.createRating(id,request,authentication.getName());
    }
}