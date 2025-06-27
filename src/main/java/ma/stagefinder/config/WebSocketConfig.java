package ma.stagefinder.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker // HADI KAT'ACTIVER L'KHADMA DYAL WEBSOCKET F SPRING
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Hada howa l'bab l'kbir (l'URL) fin l'client (navigateur) ghay'soni l'awel merra
        // bach i'bda l'connection dyal WebSocket.
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*") // Kan'sm7o l ayi site it'connecta (mezyana f développement)
                .withSockJS(); // SockJS kaydir support l les navigateurs l'qdam
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // Hna kangolo l'serveur: ayi message jay men l'client w ghadi l chi blassa
        // f l'backend, l'3onwan dyalo ghaybda b "/app"
        registry.setApplicationDestinationPrefixes("/app");

        // Hna kangolo: ayi message kharej men 3ndna (men l'backend) w ghadi l l'client,
        // ghankhdmo b "broker" bassit, w les 9anawat (channels) ghaybdaw b /topic wla /queue
        registry.enableSimpleBroker("/topic", "/queue");

        // Hada sarout mohim: bach n9dro nsifto message l user we7d b'ضبط,
        // kan'definiw l'prefix "/user".
        registry.setUserDestinationPrefix("/user");
    }
}