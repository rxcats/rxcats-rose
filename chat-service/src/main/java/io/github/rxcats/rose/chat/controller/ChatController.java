package io.github.rxcats.rose.chat.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.socket.WebSocketSession;

import io.github.rxcats.rose.chat.model.message.JoinRoomRequest;
import io.github.rxcats.rose.chat.model.message.SendMessageRequest;
import io.github.rxcats.rose.chat.service.ChatService;
import io.github.rxcats.rose.chat.service.SessionService;
import io.github.rxcats.rose.chat.ws.WsSessionWrapper;
import io.github.rxcats.rose.chat.ws.annotation.WsController;
import io.github.rxcats.rose.chat.ws.annotation.WsMethod;
import io.github.rxcats.rose.chat.ws.annotation.WsRequestBody;
import io.github.rxcats.rose.chat.ws.annotation.WsSession;

/**
 * createRoom
 * {"uri":"/chat/v1/createRoom", "body":{}}
 *
 * joinRoom
 * {"uri":"/chat/v1/joinRoom", "body":{}}
 *
 * leaveRoom
 * {"uri":"/chat/v1/leaveRoom", "body":{}}
 *
 * sendMessage
 * {"uri":"/chat/v1/sendMessage", "body":{"message":"hello everyone!!!"}}
 */
@WsController(prefix = "/chat/v1")
public class ChatController {

    @Autowired
    SessionService sessionService;

    @Autowired
    ChatService chatService;

    @WsMethod(uri = "/createRoom")
    public void createRoom(@WsSession WebSocketSession session) {
        WsSessionWrapper wrapper = sessionService.checkJoinRoomAndGet(session);
        chatService.createRoom(wrapper);
    }

    @WsMethod(uri = "/joinRoom")
    public void joinRoom(@WsSession WebSocketSession session, @WsRequestBody JoinRoomRequest request) {
        WsSessionWrapper wrapper = sessionService.checkJoinRoomAndGet(session);
        chatService.joinRoom(wrapper, request.getRoomId());
    }

    @WsMethod(uri = "/sendMessage")
    public void sendMessage(@WsSession WebSocketSession session, @Valid @WsRequestBody SendMessageRequest request) {
        WsSessionWrapper wrapper = sessionService.checkLoginAndGet(session);
        chatService.sendMessage(wrapper, request.getMessage());
    }

    @WsMethod(uri = "/leaveRoom")
    public void leaveRoom(@WsSession WebSocketSession session) {
        WsSessionWrapper wrapper = sessionService.checkLoginAndGet(session);
        chatService.leaveRoom(wrapper, false);
    }

}
