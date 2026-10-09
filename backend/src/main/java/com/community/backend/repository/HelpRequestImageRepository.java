package com.community.backend.repository;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.community.backend.entity.HelpRequest;
import com.community.backend.entity.HelpRequestImage;
public interface HelpRequestImageRepository extends JpaRepository<HelpRequestImage,Long> {
    List<HelpRequestImage> findByHelpRequestOrderByCreatedAtAsc(HelpRequest helpRequest);
    long countByHelpRequest(HelpRequest helpRequest);
    boolean existsByHelpRequestAndImageUrl(HelpRequest helpRequest,String imageUrl);
}
