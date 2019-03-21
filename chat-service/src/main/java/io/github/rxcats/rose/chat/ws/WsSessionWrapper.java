package io.github.rxcats.rose.chat.ws;

import org.springframework.web.socket.WebSocketSession;

import lombok.Data;

import io.github.rxcats.rose.chat.ws.model.WsUser;

@Data
public class WsSessionWrapper {

    private String sessionId;

    private WebSocketSession session;

    private long createdTime;

    private WsUser user;

    public WsSessionWrapper(WebSocketSession session) {
        this.session = session;
        this.sessionId = session.getId();
        this.createdTime = System.currentTimeMillis();
    }

    public void setLogin(String userId) {
        if (this.user == null) {
            this.user = new WsUser();
        }
        this.user.setUserId(userId);
        this.user.setLoginTime(System.currentTimeMillis());
    }

    public void joinRoom(String roomId) {
        this.user.setRoomId(roomId);
        this.user.setJoinTime(System.currentTimeMillis());
    }

    public void leaveRoom() {
        this.user.setRoomId(null);
        this.user.setJoinTime(null);
    }
}
