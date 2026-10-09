package com.community.backend.repository;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.community.backend.entity.Report;
import com.community.backend.entity.ReportStatus;
public interface ReportRepository extends JpaRepository<Report,Long> {
    List<Report> findByStatusOrderByCreatedAtDesc(ReportStatus status);
    List<Report> findAllByOrderByCreatedAtDesc();
}