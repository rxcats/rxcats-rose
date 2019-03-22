package io.github.rxcats.rose.chat.controller;

import java.io.IOException;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.socket.WebSocketSession;

import io.github.rxcats.rose.chat.model.message.LoginRequest;
import io.github.rxcats.rose.chat.model.result.CheckJoinRoomResult;
import io.github.rxcats.rose.chat.service.ChatService;
import io.github.rxcats.rose.chat.service.SessionService;
import io.github.rxcats.rose.chat.ws.annotation.WsController;
import io.github.rxcats.rose.chat.ws.annotation.WsMethod;
import io.github.rxcats.rose.chat.ws.annotation.WsRequestBody;
import io.github.rxcats.rose.chat.ws.annotation.WsSession;

/**
 * login
 * {"uri":"/session/v1/login", "body":{"userId":"1000000001"}}
 *
 * logout
 * {"uri":"/session/v1/logout", "body":{}}
 */
@WsController(prefix = "/session/v1")
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
        CheckJoinRoomResult rs = sessionService.checkJoinRoomAndLogout(session);

        try {
            session.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (rs.isJoined()) {
            chatService.leaveRoom(rs.getWsSessionWrapper(), true);
        }
    }

}
