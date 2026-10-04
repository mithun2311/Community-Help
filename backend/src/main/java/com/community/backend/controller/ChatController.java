package com.community.backend.controller;
import java.util.List;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.community.backend.dto.ChatMessageRequest;
import com.community.backend.entity.Chat;
import com.community.backend.entity.ChatMessage;
import com.community.backend.service.ChatService;
@RestController
@RequestMapping("/api")
public class ChatController {
    private ChatService chatService;
    public ChatController(ChatService chatService) {
        this.chatService=chatService;
    }
    @PostMapping("/help-requests/{id}/chat")
    public Chat getOrCreateChat(@PathVariable Long id) {
        return chatService.getOrCreateChat(id,getEmail());
    }
    @GetMapping("/chats/{id}/messages")
    public List<ChatMessage> getMessages(@PathVariable Long id) {
        return chatService.getMessages(id,getEmail());
    }
    @PostMapping("/chats/{id}/messages")
    public ChatMessage sendMessage(@PathVariable Long id,@RequestBody ChatMessageRequest request) {
        return chatService.sendMessage(id,request,getEmail());
    }
    @PutMapping("/chats/{id}/end")
    public Chat endChat(@PathVariable Long id) {
        return chatService.endChat(id,getEmail());
    }
    @PutMapping("/chats/{id}/block")
    public Chat blockChat(@PathVariable Long id) {
        return chatService.blockChat(id,getEmail());
    }
    private String getEmail() {
        Authentication authentication=SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }
}