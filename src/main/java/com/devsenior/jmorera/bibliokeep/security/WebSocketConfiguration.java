package com.devsenior.jmorera.bibliokeep.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfiguration implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // Habilita un broker simple en memoria para enviar mensajes al cliente
        // Los clientes se suscribirán a rutas que empiecen con /topic
        config.enableSimpleBroker("/topic");

        // Prefijo para los mensajes que el cliente envía al servidor (@MessageMapping)
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // El punto de entrada para la conexión WebSocket inicial
        registry.addEndpoint("/ws")
                .setAllowedOrigins("*")
                .withSockJS(); // Ajustar según seguridad en producción

    }
}