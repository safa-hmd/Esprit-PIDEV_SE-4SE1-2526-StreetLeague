package com.example.streetleague.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
<<<<<<< HEAD
import org.springframework.web.socket.config.annotation.WebSocketTransportRegistration;
=======
>>>>>>> d97c24f7ac7e148ae108ca34ae4d7f2e7dd375a5

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
<<<<<<< HEAD
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic");
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws-chat")
                .setAllowedOriginPatterns("*")
                .withSockJS();
    }

    // CRITIQUE : Augmenter la taille max des frames pour les messages audio Base64
    @Override
    public void configureWebSocketTransport(WebSocketTransportRegistration registration) {
        registration.setMessageSizeLimit(10 * 1024 * 1024);   // 10 MB
        registration.setSendBufferSizeLimit(10 * 1024 * 1024); // 10 MB
        registration.setSendTimeLimit(20000); // 20 secondes
    }
}
=======
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*") // Requis pour SockJS + CORS
                .withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/topic");
        registry.setApplicationDestinationPrefixes("/app");
    }
}
>>>>>>> d97c24f7ac7e148ae108ca34ae4d7f2e7dd375a5
