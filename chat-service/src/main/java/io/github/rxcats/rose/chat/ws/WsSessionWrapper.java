package io.github.rxcats.rose.chat.ws;

import org.springframework.web.socket.WebSocketSession;

import lombok.Data;

@Data
public class WsSessionWrapper {

    private WebSocketSession session;

    private String sessionId;

    private long createdAt;

    private String userId;

    private Long loginAt;

    private String roomId;

    private Long joinAt;

    public WsSessionWrapper(WebSocketSession session) {
        this.session = session;
        this.sessionId = session.getId();
        this.createdAt = System.currentTimeMillis();
    }

    public void setLogin(String userId) {
        this.userId = userId;
        this.loginAt = System.currentTimeMillis();
    }

    public void joinRoom(String roomId) {
        this.roomId = roomId;
        this.joinAt = System.currentTimeMillis();
    }

    public void leaveRoom() {
        this.roomId = null;
        this.joinAt = null;
    }
}
