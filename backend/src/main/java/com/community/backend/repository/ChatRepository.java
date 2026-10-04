package com.community.backend.repository;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.community.backend.entity.Chat;
import com.community.backend.entity.HelpRequest;
public interface ChatRepository extends JpaRepository<Chat,Long> {
    Optional<Chat> findByHelpRequest(HelpRequest helpRequest);
}