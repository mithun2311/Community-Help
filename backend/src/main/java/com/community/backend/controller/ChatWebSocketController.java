package com.community.backend.controller;
import java.security.Principal;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;
import com.community.backend.dto.ChatMessageRequest;
import com.community.backend.dto.ChatMessageResponse;
import com.community.backend.service.ChatService;
import jakarta.validation.Valid;
@Controller
public class ChatWebSocketController {
    private final ChatService chatService;
    public ChatWebSocketController(ChatService chatService) { this.chatService=chatService; }
    @MessageMapping("/chats/{id}/send")
    public void sendMessage(@DestinationVariable Long id,@Valid @Payload ChatMessageRequest request,Principal principal) {
        if(principal==null) throw new org.springframework.security.access.AccessDeniedException("Authentication is required");
        chatService.sendMessage(id,request,principal.getName());
    }
}
