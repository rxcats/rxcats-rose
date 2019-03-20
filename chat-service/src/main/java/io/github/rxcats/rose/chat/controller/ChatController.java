package io.github.rxcats.rose.chat.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.socket.WebSocketSession;

import io.github.rxcats.rose.chat.constant.Define;
import io.github.rxcats.rose.chat.model.message.LoginRequest;
import io.github.rxcats.rose.chat.model.message.SendMessageRequest;
import io.github.rxcats.rose.chat.service.ChatService;
import io.github.rxcats.rose.chat.ws.WsSessionManager;
import io.github.rxcats.rose.chat.ws.WsSessionWrapper;
import io.github.rxcats.rose.chat.ws.annotation.WsController;
import io.github.rxcats.rose.chat.ws.annotation.WsMethod;
import io.github.rxcats.rose.chat.ws.annotation.WsRequestBody;
import io.github.rxcats.rose.chat.ws.annotation.WsSession;
import io.github.rxcats.rose.chat.ws.service.SessionService;

/**
 * login
 * {"uri":"/chat/v1/login", "body":{"userId":"1000000001"}}
 *
 * createRoom
 * {"uri":"/chat/v1/createRoom", "body":{}}
 *
 * joinRoom
 * {"uri":"/chat/v1/joinRoom", "body":{}}
 *
 * sendMessage
 * {"uri":"/chat/v1/sendMessage", "body":{"message":"hello everyone!!!"}}
 *
 * logout
 * {"uri":"/chat/v1/logout", "body":{"userId":"1000000001"}}
 */
@WsController(prefix = Define.CHAT_URI_PREFIX, groupName = "chatGroup"/*, threadSize = 10, queueSize = 50, timeout = 3000*/)
public class ChatController {

    @Autowired
    SessionService sessionService;

    @Autowired
    ChatService chatService;

    @WsMethod(uri = "/login")
    public void login(@WsSession WebSocketSession session, @Valid @WsRequestBody LoginRequest request) {
        sessionService.login(session, request.getUserId());
    }

    @WsMethod(uri = "/createRoom")
    public void createRoom(@WsSession WebSocketSession session) {
        WsSessionWrapper wrapper = WsSessionManager.get(session.getId());
        if (wrapper != null) {
            chatService.createRoom(wrapper.getSessionId(), wrapper.getUserId());
            sessionService.joinRoom(session, Define.DEMO_ROOM_ID);
        }
    }

    @WsMethod(uri = "/joinRoom")
    public void joinRoom(@WsSession WebSocketSession session) {
        WsSessionWrapper wrapper = WsSessionManager.get(session.getId());
        if (wrapper != null) {
            chatService.joinRoom(wrapper.getSessionId(), wrapper.getUserId());
            sessionService.joinRoom(session, Define.DEMO_ROOM_ID);
        }
    }

    @WsMethod(uri = "/sendMessage")
    public void sendMessage(@WsSession WebSocketSession session, @Valid @WsRequestBody SendMessageRequest request) {
        WsSessionWrapper wrapper = WsSessionManager.get(session.getId());
        if (wrapper != null) {
            chatService.sendMessage(wrapper.getUserId(), request.getMessage());
        }
    }

    @WsMethod(uri = "/leaveRoom")
    public void leaveRoom(@WsSession WebSocketSession session) {
        WsSessionWrapper wrapper = WsSessionManager.get(session.getId());
        if (wrapper != null) {
            chatService.leaveRoom(wrapper.getSessionId());
            sessionService.leaveRoom(session);
        }
    }

    @WsMethod(uri = "/logout")
    public void logout(@WsSession WebSocketSession session) {
        chatService.leaveRoom(session.getId());
        sessionService.logout(session);
    }

}
