package com.community.backend.repository;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.community.backend.entity.HelpRequest;
import com.community.backend.entity.TrackingLocation;
public interface TrackingLocationRepository extends JpaRepository<TrackingLocation,Long> {
    Optional<TrackingLocation> findTopByHelpRequestOrderByRecordedAtDesc(HelpRequest helpRequest);
}