package com.community.backend.repository;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.community.backend.entity.IdempotencyRecord;
public interface IdempotencyRecordRepository extends JpaRepository<IdempotencyRecord,Long> {
    Optional<IdempotencyRecord> findByIdempotencyKeyAndUserEmailAndOperation(String idempotencyKey,String userEmail,String operation);
}
