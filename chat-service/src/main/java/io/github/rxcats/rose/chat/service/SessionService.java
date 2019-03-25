package io.github.rxcats.rose.chat.service;

import javax.annotation.PreDestroy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;

import io.github.rxcats.rose.chat.exception.ServiceException;
import io.github.rxcats.rose.chat.model.UserInfo;
import io.github.rxcats.rose.chat.model.result.CheckJoinRoomResult;
import io.github.rxcats.rose.chat.ws.WsSessionManager;
import io.github.rxcats.rose.chat.ws.WsSessionWrapper;

@Service
public class SessionService {

    @Autowired
    UserService userService;

    @PreDestroy
    public void destroy() {
        WsSessionManager.clean();
        WsSessionManager.cleanClosed();
    }

    public CheckJoinRoomResult checkJoinRoomAndLogout(WebSocketSession session) {
        var result = new CheckJoinRoomResult();

        WsSessionWrapper wrapper = this.checkLoginAndGet(session);
        if (wrapper.getUser().getRoomId() != null) {
            result.setJoined(true);
            result.setWsSessionWrapper(wrapper);
        }

        WsSessionManager.remove(session.getId());

        return result;
    }

    public void login(WebSocketSession session, String userId) {
        var wrapper = WsSessionManager.get(session.getId());
        if (wrapper != null) {
            throw new ServiceException("session already logged in");
        } else {
            UserInfo userInfo = userService.get(userId);
            if (userInfo == null) {
                throw new ServiceException("could not find user");
            }
            wrapper = new WsSessionWrapper(session);
            wrapper.setLogin(userId, userInfo.getUsername(), userInfo.getAvatar());
            WsSessionManager.put(session.getId(), wrapper);
        }
    }

    public WsSessionWrapper checkLoginAndGet(WebSocketSession session) {
        var wrapper = WsSessionManager.get(session.getId());
        if (wrapper == null) {
            throw new ServiceException("session is not yet logged in");
        }

        if (wrapper.getUser().getRoomId() == null) {
            throw new ServiceException("session is not yet joined room");
        }

        return wrapper;
    }

    public WsSessionWrapper get(WebSocketSession session) {
        return WsSessionManager.get(session.getId());
    }

    public WsSessionWrapper checkJoinRoomAndGet(WebSocketSession session) {
        var wrapper = WsSessionManager.get(session.getId());
        if (wrapper == null) {
            throw new ServiceException("session is not yet logged in");
        }

        if (wrapper.getUser().getRoomId() != null) {
            throw new ServiceException("session already joined room");
        }

        return wrapper;
    }

}
