package com.community.backend.service;
import java.time.LocalDateTime;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.community.backend.dto.VerificationRequest;
import com.community.backend.dto.VerificationResponse;
import com.community.backend.entity.User;
import com.community.backend.entity.VerificationStatus;
import com.community.backend.exception.ResourceNotFoundException;
import com.community.backend.repository.UserRepository;
@Service
public class VerificationService {
    private final UserRepository userRepository;
    public VerificationService(UserRepository userRepository) { this.userRepository=userRepository; }
    @Transactional
    public VerificationResponse requestVerification(String email,VerificationRequest request) {
        User user=userRepository.findByEmail(email).orElseThrow(()->new ResourceNotFoundException("User not found"));
        if(user.getVerificationStatus()==VerificationStatus.VERIFIED) throw new IllegalStateException("This account is already verified");
        user.setVerificationStatus(VerificationStatus.PENDING);
        user.setVerificationNote(request.getNote().trim());
        user.setVerificationRequestedAt(LocalDateTime.now());
        user=userRepository.save(user);
        return toResponse(user);
    }
    public VerificationResponse getStatus(String email) {
        User user=userRepository.findByEmail(email).orElseThrow(()->new ResourceNotFoundException("User not found"));
        return toResponse(user);
    }
    private VerificationResponse toResponse(User user) {
        return new VerificationResponse(user.getVerificationStatus(),user.getVerificationNote(),user.getVerificationRequestedAt());
    }
}
