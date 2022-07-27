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
        registry.addEndpoint("/mywebsockets")
                .setAllowedOrigins("https://*.canterbury.ac.nz")
                .withSockJS()
                .setClientLibraryUrl("webjars/sockjs-client/1.5.1/sockjs.min.js");
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // Sets up broker for endpoint
        config.enableSimpleBroker("/test/portfolio/artefact");
        // Sets up endpoint the application back end is listening to (Where the front-end sends to)
        config.setApplicationDestinationPrefixes("/test/portfolio/app");
    }

}
