package com.community.backend.repository;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.community.backend.entity.HelpRequest;
import com.community.backend.entity.HelpRequestTag;
public interface HelpRequestTagRepository extends JpaRepository<HelpRequestTag,Long> {
    List<HelpRequestTag> findByHelpRequestOrderByTagAsc(HelpRequest helpRequest);
    boolean existsByHelpRequestAndTag(HelpRequest helpRequest,String tag);
    void deleteByHelpRequest(HelpRequest helpRequest);
}
