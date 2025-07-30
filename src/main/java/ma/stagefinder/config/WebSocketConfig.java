package ma.stagefinder.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.security.config.annotation.web.messaging.MessageSecurityMetadataSourceRegistry;
import org.springframework.security.config.annotation.web.socket.AbstractSecurityWebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig extends AbstractSecurityWebSocketMessageBrokerConfigurer implements WebSocketMessageBrokerConfigurer  {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // Activer un courtier de messages simple pour envoyer des messages aux clients abonnés
        config.enableSimpleBroker("/topic", "/queue");
        // Préfixe pour les messages envoyés par les clients
        config.setApplicationDestinationPrefixes("/app");
        // Préfixe pour les messages spécifiques aux utilisateurs
        config.setUserDestinationPrefix("/user");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        System.out.println("Registering WebSocket endpoint /ws");
        registry.addEndpoint("/ws")
                .setAllowedOrigins("http://localhost:4200")
                .withSockJS();
    }

    @Override
    protected void configureInbound(MessageSecurityMetadataSourceRegistry messages) {
        messages
                .simpDestMatchers("/app/**").authenticated()
                .simpSubscribeDestMatchers("/user/**", "/topic/**", "/queue/**").authenticated()
                .anyMessage().authenticated();
    }

    @Override
    protected boolean sameOriginDisabled() {
        return true; // Permettre les connexions cross-origin pour localhost:4200
    }


}