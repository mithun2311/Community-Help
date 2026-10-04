package com.community.backend.repository;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.community.backend.entity.HelpRequest;
import com.community.backend.entity.SafetyCheckIn;
import com.community.backend.entity.User;
public interface SafetyCheckInRepository extends JpaRepository<SafetyCheckIn,Long> {
    Optional<SafetyCheckIn> findTopByHelpRequestAndUserOrderByCreatedAtDesc(HelpRequest helpRequest,User user);
}