package nz.ac.canterbury.seng302.portfolio;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import java.io.File;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        File file = new File("");
        String url = file.getAbsolutePath();
        String endpointName = "/test/portfolio/mywebsockets";
        if (url.contains("test") || url.contains("prod") || url.contains("canterbury")) {
            endpointName = "/mywebsockets";
        }
        // Endpoint all websockets are set up at
        registry.addEndpoint(endpointName)
                .setAllowedOrigins("csse-s302g1.canterbury.ac.nz", "localhost:9000")
                .withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // Sets up broker for endpoint
        config.enableSimpleBroker("/test/portfolio/artefact");
        // Sets up endpoint the application back end is listening to (Where the front-end sends to)
        config.setApplicationDestinationPrefixes("/test/portfolio/app");
    }

}
