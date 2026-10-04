package com.community.backend.repository;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.community.backend.entity.HelpRequest;
import com.community.backend.entity.SOSIncident;
public interface SOSIncidentRepository extends JpaRepository<SOSIncident,Long> {
    Optional<SOSIncident> findTopByHelpRequestOrderByCreatedAtDesc(HelpRequest helpRequest);
}