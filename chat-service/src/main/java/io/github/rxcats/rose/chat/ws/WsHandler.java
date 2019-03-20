package io.github.rxcats.rose.chat.ws;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.PongMessage;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import lombok.extern.slf4j.Slf4j;

import io.github.rxcats.rose.chat.service.ChatService;
import io.github.rxcats.rose.chat.ws.service.SessionService;

@Slf4j
@Component
public class WsHandler extends TextWebSocketHandler {

    @Autowired
    WsMessageConverter wsMessageConverter;

    @Autowired
    SessionService sessionService;

    @Autowired
    ChatService chatService;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        log.info("connection established");
        log.info("session : {}", session);
        sessionService.open(session);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        log.info("receive message");
        log.info("session : {}", session);
        log.info("message : {}", message);
        wsMessageConverter.execute(session, message.getPayload());
    }

    @Override
    protected void handlePongMessage(WebSocketSession session, PongMessage message) throws Exception {
        log.info("receive pong");
        log.info("session : {}", session);
        log.info("message : {}", message);
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        log.warn("transport error");
        log.info("session : {}", session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        log.info("connection closed");
        log.info("session : {}", session);
        chatService.leaveRoom(session.getId());
        sessionService.close(session);
    }
}
