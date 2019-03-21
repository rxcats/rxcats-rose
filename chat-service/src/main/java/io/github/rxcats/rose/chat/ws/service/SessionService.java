package io.github.rxcats.rose.chat.ws.service;

import java.io.IOException;

import javax.annotation.PreDestroy;

import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;

import io.github.rxcats.rose.chat.exception.ServiceException;
import io.github.rxcats.rose.chat.ws.WsSessionManager;
import io.github.rxcats.rose.chat.ws.WsSessionWrapper;

@Service
public class SessionService {

    @PreDestroy
    public void destroy() {
        WsSessionManager.clean();
    }

    public void open(WebSocketSession session) {

    }

    public void close(WebSocketSession session) {
        WsSessionManager.remove(session.getId());
    }

    public boolean checkJoinRoomAndLogout(WebSocketSession session) {
        boolean isJoinRoom = false;

        WsSessionWrapper wrapper = this.checkLoginAndGet(session);
        if (wrapper.getUser().getRoomId() != null) {
            isJoinRoom = true;
        }

        WsSessionManager.remove(session.getId());

        try {
            session.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return isJoinRoom;
    }

    public void login(WebSocketSession session, String userId) {
        var wrapper = WsSessionManager.get(session.getId());
        if (wrapper != null) {
            throw new ServiceException("session already logged in");
        } else {
            wrapper = new WsSessionWrapper(session);
            wrapper.setLogin(userId);
            WsSessionManager.put(session.getId(), wrapper);
        }
    }

    public WsSessionWrapper checkLoginAndGet(WebSocketSession session) {
        var wrapper = WsSessionManager.get(session.getId());
        if (wrapper == null) {
            throw new ServiceException("session is not yet logged in");
        }
        return wrapper;
    }

    public WsSessionWrapper get(WebSocketSession session) {
        return WsSessionManager.get(session.getId());
    }

    public WsSessionWrapper joinRoom(WebSocketSession session, String roomId) {
        var wrapper = this.checkLoginAndGet(session);
        wrapper.joinRoom(roomId);
        return wrapper;
    }

    public WsSessionWrapper leaveRoom(WebSocketSession session) {
        var wrapper = this.checkLoginAndGet(session);
        wrapper.leaveRoom();
        return wrapper;
    }

}
