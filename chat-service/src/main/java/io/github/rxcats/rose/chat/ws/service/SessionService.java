package io.github.rxcats.rose.chat.ws.service;

import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;

import io.github.rxcats.rose.chat.ws.WsSessionManager;
import io.github.rxcats.rose.chat.ws.WsSessionWrapper;

@Service
public class SessionService {

    public void open(WebSocketSession session) {
        WsSessionManager.put(session.getId(), new WsSessionWrapper(session));
    }

    public void close(WebSocketSession session) {
        WsSessionManager.remove(session.getId());
    }

    public void login(WebSocketSession session, String userId) {
        WsSessionWrapper wrapper = WsSessionManager.get(session.getId());
        wrapper.setLogin(userId);
    }

    public void joinRoom(WebSocketSession session, String roomId) {
        WsSessionWrapper wrapper = WsSessionManager.get(session.getId());
        wrapper.joinRoom(roomId);
    }

    public void leaveRoom(WebSocketSession session) {
        WsSessionWrapper wrapper = WsSessionManager.get(session.getId());
        wrapper.leaveRoom();
    }

    public void logout(WebSocketSession session) {
        WsSessionManager.remove(session.getId());
    }

}
