package com.community.backend.repository;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.community.backend.entity.HelpRequest;
import com.community.backend.entity.HelpRequestLocation;
@Repository
public interface HelpRequestLocationRepository extends JpaRepository<HelpRequestLocation,Long> {
    Optional<HelpRequestLocation> findByHelpRequest(HelpRequest helpRequest);
}