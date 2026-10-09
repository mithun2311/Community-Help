package com.community.backend.service;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import com.community.backend.dto.ChatMessageRequest;
import com.community.backend.dto.ChatMessageResponse;
import com.community.backend.entity.Chat;
import com.community.backend.entity.ChatMessage;
import com.community.backend.entity.ChatStatus;
import com.community.backend.entity.HelpRequest;
import com.community.backend.entity.HelpRequestStatus;
import com.community.backend.entity.User;
import com.community.backend.exception.ResourceNotFoundException;
import com.community.backend.exception.UnauthorizedException;
import com.community.backend.repository.ChatMessageRepository;
import com.community.backend.repository.ChatRepository;
import com.community.backend.repository.HelpRequestRepository;
import com.community.backend.repository.UserRepository;
@Service
public class ChatService {
    private ChatRepository chatRepository;
    private ChatMessageRepository chatMessageRepository;
    private HelpRequestRepository helpRequestRepository;
    private UserRepository userRepository;
    private SimpMessagingTemplate messagingTemplate;
    public ChatService(ChatRepository chatRepository,ChatMessageRepository chatMessageRepository,HelpRequestRepository helpRequestRepository,UserRepository userRepository,SimpMessagingTemplate messagingTemplate) {
        this.chatRepository=chatRepository;
        this.chatMessageRepository=chatMessageRepository;
        this.helpRequestRepository=helpRequestRepository;
        this.userRepository=userRepository;
        this.messagingTemplate=messagingTemplate;
    }
    public Chat getOrCreateChat(Long requestId,String email) {
        HelpRequest request=getRequest(requestId);
        validateParticipant(request,email);
        if(request.getHelper()==null) {
            throw new IllegalStateException("A helper must be assigned before chat can start");
        }
        if(request.getStatus()==HelpRequestStatus.COMPLETED || request.getStatus()==HelpRequestStatus.CANCELLED || request.getStatus()==HelpRequestStatus.EXPIRED || request.getStatus()==HelpRequestStatus.DISPUTED) {
            throw new IllegalStateException("Chat cannot be started for a closed help request");
        }
        return chatRepository.findByHelpRequest(request).orElseGet(()->{
            Chat chat=new Chat();
            chat.setHelpRequest(request);
            chat.setRequester(request.getCreator());
            chat.setHelper(request.getHelper());
            chat.setStatus(ChatStatus.ACTIVE);
            chat.setCreatedAt(LocalDateTime.now());
            return chatRepository.save(chat);
        });
    }
    public ChatMessage sendMessage(Long chatId,ChatMessageRequest request,String email) {
        Chat chat=getChat(chatId);
        validateParticipant(chat.getHelpRequest(),email);
        if(chat.getStatus()!=ChatStatus.ACTIVE) {
            throw new IllegalStateException("Chat is not active");
        }
        HelpRequestStatus requestStatus=chat.getHelpRequest().getStatus();
        if(requestStatus==HelpRequestStatus.COMPLETED || requestStatus==HelpRequestStatus.CANCELLED || requestStatus==HelpRequestStatus.EXPIRED || requestStatus==HelpRequestStatus.DISPUTED) {
            throw new IllegalStateException("Chat is closed because the help request has ended");
        }
        if(request.getContent()==null || request.getContent().trim().isEmpty()) {
            throw new IllegalArgumentException("Message cannot be empty");
        }
        if(request.getContent().length()>2000) {
            throw new IllegalArgumentException("Message is too long");
        }
        User sender=getUser(email);
        ChatMessage message=new ChatMessage();
        message.setChat(chat);
        message.setSender(sender);
        message.setContent(request.getContent().trim());
        message.setSentAt(LocalDateTime.now());
        message=chatMessageRepository.save(message);
        messagingTemplate.convertAndSend("/topic/chats/"+chat.getId(),ChatMessageResponse.from(message));
        return message;
    }
    public List<ChatMessage> getMessages(Long chatId,String email) {
        Chat chat=getChat(chatId);
        validateParticipant(chat.getHelpRequest(),email);
        return chatMessageRepository.findByChatOrderBySentAtAsc(chat);
    }
    public Chat endChat(Long chatId,String email) {
        Chat chat=getChat(chatId);
        validateParticipant(chat.getHelpRequest(),email);
        if(chat.getStatus()==ChatStatus.ENDED) {
            return chat;
        }
        chat.setStatus(ChatStatus.ENDED);
        chat.setEndedAt(LocalDateTime.now());
        return chatRepository.save(chat);
    }
    public Chat blockChat(Long chatId,String email) {
        Chat chat=getChat(chatId);
        validateParticipant(chat.getHelpRequest(),email);
        chat.setStatus(ChatStatus.BLOCKED);
        chat.setEndedAt(LocalDateTime.now());
        return chatRepository.save(chat);
    }
    private Chat getChat(Long id) {
        return chatRepository.findById(id).orElseThrow(()->new ResourceNotFoundException("Chat not found"));
    }
    private HelpRequest getRequest(Long id) {
        return helpRequestRepository.findById(id).orElseThrow(()->new ResourceNotFoundException("Help request not found"));
    }
    private User getUser(String email) {
        return userRepository.findByEmail(email).orElseThrow(()->new ResourceNotFoundException("User not found"));
    }
    private void validateParticipant(HelpRequest request,String email) {
        boolean requester=request.getCreator().getEmail().equals(email);
        boolean helper=request.getHelper()!=null && request.getHelper().getEmail().equals(email);
        if(!requester && !helper) {
            throw new UnauthorizedException("User is not authorized for this chat");
        }
        if(request.getStatus()==HelpRequestStatus.CANCELLED || request.getStatus()==HelpRequestStatus.EXPIRED) {
            throw new IllegalStateException("Chat is unavailable for this request");
        }
    }
}