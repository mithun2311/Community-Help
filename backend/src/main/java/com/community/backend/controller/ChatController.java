package com.community.backend.controller;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.community.backend.dto.ChatMessageRequest;
import com.community.backend.dto.ChatMessageResponse;
import com.community.backend.dto.ChatResponse;
import com.community.backend.service.ChatService;
import jakarta.validation.Valid;
@RestController
@RequestMapping("/api")
public class ChatController {
    private final ChatService chatService;
    public ChatController(ChatService chatService) { this.chatService=chatService; }
    @PostMapping("/help-requests/{id}/chat")
    public ChatResponse getOrCreateChat(@PathVariable Long id,Authentication authentication) { return ChatResponse.from(chatService.getOrCreateChat(id,authentication.getName())); }
    @GetMapping("/chats/{id}/messages")
    public List<ChatMessageResponse> getMessages(@PathVariable Long id,Authentication authentication) { return chatService.getMessages(id,authentication.getName()).stream().map(ChatMessageResponse::from).collect(Collectors.toList()); }
    @PostMapping("/chats/{id}/messages")
    public ChatMessageResponse sendMessage(@PathVariable Long id,@Valid @RequestBody ChatMessageRequest request,Authentication authentication) { return ChatMessageResponse.from(chatService.sendMessage(id,request,authentication.getName())); }
    @PutMapping("/chats/{id}/end")
    public ChatResponse endChat(@PathVariable Long id,Authentication authentication) { return ChatResponse.from(chatService.endChat(id,authentication.getName())); }
    @PutMapping("/chats/{id}/block")
    public ChatResponse blockChat(@PathVariable Long id,Authentication authentication) { return ChatResponse.from(chatService.blockChat(id,authentication.getName())); }
}
