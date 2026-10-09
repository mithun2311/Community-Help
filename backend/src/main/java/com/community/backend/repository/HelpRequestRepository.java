package com.community.backend.repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import jakarta.persistence.LockModeType;
import com.community.backend.entity.HelpRequest;
import com.community.backend.entity.HelpRequestStatus;
@Repository
public interface HelpRequestRepository extends JpaRepository<HelpRequest,Long> {
    List<HelpRequest> findAllByOrderByCreatedAtDesc();
    List<HelpRequest> findAllByStatusOrderByCreatedAtDesc(HelpRequestStatus status);
    List<HelpRequest> findAllByStatusAndExpiresAtBefore(HelpRequestStatus status,LocalDateTime time);
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select request from HelpRequest request where request.id = :id")
    Optional<HelpRequest> findByIdForUpdate(@Param("id") Long id);
}
