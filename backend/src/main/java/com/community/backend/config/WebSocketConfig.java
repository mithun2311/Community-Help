package com.community.backend.config;
import java.util.Arrays;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    private final String allowedOrigins;
    private final WebSocketAuthInterceptor authInterceptor;
    public WebSocketConfig(@Value("${app.cors.allowed-origins:http://localhost:3000,http://localhost:5173}") String allowedOrigins,WebSocketAuthInterceptor authInterceptor) {
        this.allowedOrigins=allowedOrigins;
        this.authInterceptor=authInterceptor;
    }
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/topic");
        registry.setApplicationDestinationPrefixes("/app");
        registry.setUserDestinationPrefix("/user");
    }
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        List<String> origins=Arrays.stream(allowedOrigins.split(",")).map(String::trim).filter(value->!value.isEmpty()).toList();
        registry.addEndpoint("/ws").setAllowedOrigins(origins.toArray(String[]::new));
    }
    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(authInterceptor);
    }
}
