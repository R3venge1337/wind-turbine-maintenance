package pl.zimnya.wind_turbine_maintenance.common;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // Tu będą trafiać dane z serwera do Angulara (np. /topic/turbines)
        config.enableSimpleBroker("/topic");
        // Prefiks dla wiadomości wysyłanych z Angulara do serwera
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Punkt połączenia dla Angulara.
        // setAllowedOriginPatterns("*") jest ważne przy lokalnym developmencie (różne porty)
        registry.addEndpoint("/ws-energy").setAllowedOriginPatterns("*").withSockJS();
    }
}
