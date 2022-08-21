package nz.ac.canterbury.seng302.portfolio;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Endpoint all websockets are set up at
        registry.addEndpoint("mywebsockets")
                .setAllowedOriginPatterns("https://*.canterbury.ac.nz")
                .withSockJS()
                .setClientLibraryUrl("https://cdn.jsdelivr.net/sockjs/1.1.2/sockjs.min.js");
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // Sets up brokers for endpoint
        config.enableSimpleBroker("/webSocketGet");
        // Sets up endpoint the application back end is listening to (Where the front-end sends to)
        config.setApplicationDestinationPrefixes("/webSocketPost");
    }

}
