package com.community.backend.repository;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.community.backend.entity.Chat;
import com.community.backend.entity.ChatMessage;
public interface ChatMessageRepository extends JpaRepository<ChatMessage,Long> {
    List<ChatMessage> findByChatOrderBySentAtAsc(Chat chat);
}