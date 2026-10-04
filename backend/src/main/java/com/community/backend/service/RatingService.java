package com.community.backend.service;
import java.time.LocalDateTime;
import org.springframework.stereotype.Service;
import com.community.backend.dto.RatingRequest;
import com.community.backend.entity.HelpRequest;
import com.community.backend.entity.HelpRequestStatus;
import com.community.backend.entity.Rating;
import com.community.backend.entity.User;
import com.community.backend.exception.ResourceNotFoundException;
import com.community.backend.exception.UnauthorizedException;
import com.community.backend.repository.HelpRequestRepository;
import com.community.backend.repository.RatingRepository;
import com.community.backend.repository.UserRepository;
@Service
public class RatingService {
    private RatingRepository ratingRepository;
    private HelpRequestRepository helpRequestRepository;
    private UserRepository userRepository;
    public RatingService(RatingRepository ratingRepository,HelpRequestRepository helpRequestRepository,UserRepository userRepository) {
        this.ratingRepository=ratingRepository;
        this.helpRequestRepository=helpRequestRepository;
        this.userRepository=userRepository;
    }
    public Rating createRating(Long requestId,RatingRequest request,String email) {
        HelpRequest helpRequest=helpRequestRepository.findById(requestId).orElseThrow(()->new ResourceNotFoundException("Help request not found"));
        User rater=getUser(email);
        if(helpRequest.getStatus()!=HelpRequestStatus.COMPLETED) {
            throw new IllegalStateException("Rating is available only after completion");
        }
        boolean requester=helpRequest.getCreator().getEmail().equals(email);
        boolean helper=helpRequest.getHelper()!=null && helpRequest.getHelper().getEmail().equals(email);
        if(!requester && !helper) {
            throw new UnauthorizedException("User is not authorized to rate this request");
        }
        User ratedUser=requester ? helpRequest.getHelper() : helpRequest.getCreator();
        if(ratedUser==null) {
            throw new IllegalStateException("No user available to rate");
        }
        if(request.getScore()==null || request.getScore()<1 || request.getScore()>5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }
        if(ratingRepository.findByHelpRequestAndRater(helpRequest,rater).isPresent()) {
            throw new IllegalStateException("You have already rated this request");
        }
        Rating rating=new Rating();
        rating.setHelpRequest(helpRequest);
        rating.setRater(rater);
        rating.setRatedUser(ratedUser);
        rating.setScore(request.getScore());
        rating.setFeedback(request.getFeedback());
        rating.setCreatedAt(LocalDateTime.now());
        return ratingRepository.save(rating);
    }
    private User getUser(String email) {
        return userRepository.findByEmail(email).orElseThrow(()->new ResourceNotFoundException("User not found"));
    }
}