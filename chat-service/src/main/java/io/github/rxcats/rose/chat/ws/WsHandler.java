package io.github.rxcats.rose.chat.ws;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.PongMessage;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class WsHandler extends TextWebSocketHandler {

    @Autowired
    WsMessageConverter wsMessageConverter;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        log.info("connection established [session:{}]", session);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        log.info("receive message : [session:{}] [message:{}]", session, message.getPayload());
        wsMessageConverter.execute(session, message.getPayload());
    }

    @Override
    protected void handlePongMessage(WebSocketSession session, PongMessage message) throws Exception {
        log.info("receive pong : [session:{}] [message:{}]", session, message);
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        log.error("transport error : [session:{}]", session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        log.info("connection closed : [session:{}]", session);
        WsSessionManager.remove(session.getId()); // first remove session
        // chatService.leaveRoom(session.getRoomId()); // then leave event to broadcast.
    }
}
