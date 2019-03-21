package io.github.rxcats.rose.chat.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.socket.WebSocketSession;

import io.github.rxcats.rose.chat.constant.Define;
import io.github.rxcats.rose.chat.model.message.LoginRequest;
import io.github.rxcats.rose.chat.service.ChatService;
import io.github.rxcats.rose.chat.ws.annotation.WsController;
import io.github.rxcats.rose.chat.ws.annotation.WsMethod;
import io.github.rxcats.rose.chat.ws.annotation.WsRequestBody;
import io.github.rxcats.rose.chat.ws.annotation.WsSession;
import io.github.rxcats.rose.chat.ws.service.SessionService;

/**
 * login
 * {"uri":"/session/v1/login", "body":{"userId":"1000000001"}}
 *
 * logout
 * {"uri":"/session/v1/logout", "body":{}}
 */
@WsController(prefix = Define.SESSION_URI_PREFIX, groupName = "sessionGroup"/*, threadSize = 10, queueSize = 50, timeout = 3000*/)
public class SessionController {

    @Autowired
    SessionService sessionService;

    @Autowired
    ChatService chatService;

    @WsMethod(uri = "/login")
    public void login(@WsSession WebSocketSession session, @Valid @WsRequestBody LoginRequest request) {
        sessionService.login(session, request.getUserId());
    }

    @WsMethod(uri = "/logout")
    public void logout(@WsSession WebSocketSession session) {
        String id = session.getId();
        boolean isJoinRoom = sessionService.checkJoinRoomAndLogout(session);
        if (isJoinRoom) {
            chatService.leaveRoom(id);
        }
    }

}
