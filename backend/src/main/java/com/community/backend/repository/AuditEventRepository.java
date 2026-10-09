package com.community.backend.repository;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.community.backend.entity.AuditEvent;
public interface AuditEventRepository extends JpaRepository<AuditEvent,Long> {
    Optional<AuditEvent> findTopByHelpRequestIdOrderByIdDesc(Long helpRequestId);
    List<AuditEvent> findByHelpRequestIdOrderByIdAsc(Long helpRequestId);
}
