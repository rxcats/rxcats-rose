package io.github.rxcats.rose.chat.ws;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.standard.ServletServerContainerFactoryBean;

@EnableWebSocket
@Configuration
public class WsConfig implements WebSocketConfigurer {

    private static final int MAX_MESSAGE_SIZE = 16 * 1024 * 1024;

    @Autowired
    WsHandler handler;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(handler, "/api")
            .setAllowedOrigins("*");
    }

    @Bean
    ServletServerContainerFactoryBean servletServerContainerFactoryBean() {
        var container = new ServletServerContainerFactoryBean();
        container.setMaxTextMessageBufferSize(MAX_MESSAGE_SIZE);
        container.setMaxBinaryMessageBufferSize(MAX_MESSAGE_SIZE);
        return container;
    }

}
