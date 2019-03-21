package io.github.rxcats.rose.chat.ws.service;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

import io.github.rxcats.rose.chat.constant.Define;
import io.github.rxcats.rose.chat.model.message.WsResponseEntity;

@Slf4j
@Service
public class MessageService {

    @Autowired
    ObjectMapper objectMapper;

    private String toJson(WsResponseEntity<Object> response) {
        try {
            return objectMapper.writeValueAsString(response);
        } catch (JsonProcessingException e) {
            log.warn("write value as json error : {}", e.getMessage());
            return "";
        }
    }

    private void sendMessage(WebSocketSession session, WsResponseEntity<Object> response) {
        if (!session.isOpen()) {
            return;
        }

        try {
            String payload = toJson(response);
            log.info("response: [session:{}] [payload:{}]", session.getId(), payload);
            session.sendMessage(new TextMessage(payload));
        } catch (IOException e) {
            log.error("send message error : {}", e.getMessage());
        }
    }

    public void sendMessage(WebSocketSession session, String uri, Object message) {
        sendMessage(session, WsResponseEntity.of(uri, message));
    }

    public void sendErrorMessage(WebSocketSession session, String stack) {
        sendMessage(session, WsResponseEntity.error(Define.CHAT_ERROR_URI, stack));
    }

    public void sendErrorMessage(WebSocketSession session, String uri, String stack) {
        sendMessage(session, WsResponseEntity.error(uri, stack));
    }

}
