package com.community.backend.repository;
import java.util.Optional;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.community.backend.entity.HelpRequest;
import com.community.backend.entity.SafetyCheckIn;
import com.community.backend.entity.User;
public interface SafetyCheckInRepository extends JpaRepository<SafetyCheckIn,Long> {
    Optional<SafetyCheckIn> findTopByHelpRequestAndUserOrderByCreatedAtDesc(HelpRequest helpRequest,User user);
    List<SafetyCheckIn> findByStatusAndCreatedAtBefore(com.community.backend.entity.SafetyCheckInStatus status,LocalDateTime cutoff);
}