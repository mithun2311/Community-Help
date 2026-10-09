package com.community.backend.config;
import java.security.Principal;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;
import com.community.backend.entity.Chat;
import com.community.backend.entity.HelpRequest;
import com.community.backend.entity.HelpRequestStatus;
import com.community.backend.repository.ChatRepository;
import com.community.backend.repository.HelpRequestRepository;
import com.community.backend.repository.UserRepository;
import com.community.backend.service.JwtService;
import io.jsonwebtoken.JwtException;
@Component
public class WebSocketAuthInterceptor implements ChannelInterceptor {
    private static final Pattern CHAT_TOPIC=Pattern.compile("^/topic/chats/(\\d+)$");
    private static final Pattern CHAT_SEND=Pattern.compile("^/app/chats/(\\d+)/send$");
    private static final Pattern TRACKING_TOPIC=Pattern.compile("^/topic/requests/(\\d+)/tracking$");
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final ChatRepository chatRepository;
    private final HelpRequestRepository helpRequestRepository;
    public WebSocketAuthInterceptor(JwtService jwtService,UserRepository userRepository,ChatRepository chatRepository,HelpRequestRepository helpRequestRepository) {
        this.jwtService=jwtService; this.userRepository=userRepository; this.chatRepository=chatRepository; this.helpRequestRepository=helpRequestRepository;
    }
    @Override
    public Message<?> preSend(Message<?> message,MessageChannel channel) {
        StompHeaderAccessor accessor=MessageHeaderAccessor.getAccessor(message,StompHeaderAccessor.class);
        if(accessor==null || accessor.getCommand()==null) return message;
        if(accessor.getCommand()==StompCommand.CONNECT) {
            String authorization=accessor.getFirstNativeHeader("Authorization");
            if(authorization==null || !authorization.startsWith("Bearer ")) throw new AccessDeniedException("A bearer token is required for WebSocket connections");
            try {
                String email=jwtService.extractEmail(authorization.substring(7).trim());
                if(userRepository.findByEmail(email).isEmpty()) throw new AccessDeniedException("User account was not found");
                accessor.setUser(new UsernamePasswordAuthenticationToken(email,null,Collections.emptyList()));
            } catch(JwtException | IllegalArgumentException exception) {
                throw new AccessDeniedException("Invalid or expired WebSocket token");
            }
        }
        if(accessor.getCommand()==StompCommand.SUBSCRIBE) {
            Principal principal=accessor.getUser();
            String destination=accessor.getDestination()==null?"":accessor.getDestination();
            Matcher matcher=CHAT_TOPIC.matcher(destination);
            Matcher trackingMatcher=TRACKING_TOPIC.matcher(destination);
            if(principal==null) throw new AccessDeniedException("Subscription is not allowed");
            if(matcher.matches()) validateChatParticipant(Long.valueOf(matcher.group(1)),principal.getName());
            else if(trackingMatcher.matches()) validateTrackingParticipant(Long.valueOf(trackingMatcher.group(1)),principal.getName());
            else throw new AccessDeniedException("Subscription is not allowed");
        }
        if(accessor.getCommand()==StompCommand.SEND) {
            Principal principal=accessor.getUser();
            Matcher matcher=CHAT_SEND.matcher(accessor.getDestination()==null?"":accessor.getDestination());
            if(principal==null || !matcher.matches()) throw new AccessDeniedException("Message destination is not allowed");
            validateChatParticipant(Long.valueOf(matcher.group(1)),principal.getName());
        }
        return message;
    }
    private void validateTrackingParticipant(Long requestId,String email) {
        HelpRequest request=helpRequestRepository.findById(requestId).orElseThrow(()->new AccessDeniedException("Help request was not found"));
        boolean requester=request.getCreator().getEmail().equalsIgnoreCase(email);
        boolean helper=request.getHelper()!=null && request.getHelper().getEmail().equalsIgnoreCase(email);
        boolean closed=request.getStatus()==HelpRequestStatus.COMPLETED || request.getStatus()==HelpRequestStatus.CANCELLED || request.getStatus()==HelpRequestStatus.EXPIRED || request.getStatus()==HelpRequestStatus.DISPUTED;
        if((!requester && !helper) || closed) throw new AccessDeniedException("Tracking subscription is not allowed");
    }
    private void validateChatParticipant(Long chatId,String email) {
        Chat chat=chatRepository.findById(chatId).orElseThrow(()->new AccessDeniedException("Chat was not found"));
        boolean requester=chat.getRequester().getEmail().equalsIgnoreCase(email);
        boolean helper=chat.getHelper().getEmail().equalsIgnoreCase(email);
        if(!requester && !helper) throw new AccessDeniedException("Only chat participants may access this channel");
    }
}
