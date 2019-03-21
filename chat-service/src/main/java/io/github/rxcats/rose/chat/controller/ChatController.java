package io.github.rxcats.rose.chat.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.socket.WebSocketSession;

import io.github.rxcats.rose.chat.constant.Define;
import io.github.rxcats.rose.chat.model.message.SendMessageRequest;
import io.github.rxcats.rose.chat.service.ChatService;
import io.github.rxcats.rose.chat.ws.WsSessionWrapper;
import io.github.rxcats.rose.chat.ws.annotation.WsController;
import io.github.rxcats.rose.chat.ws.annotation.WsMethod;
import io.github.rxcats.rose.chat.ws.annotation.WsRequestBody;
import io.github.rxcats.rose.chat.ws.annotation.WsSession;
import io.github.rxcats.rose.chat.ws.service.SessionService;

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
@WsController(prefix = Define.CHAT_URI_PREFIX, groupName = "chatGroup"/*, threadSize = 10, queueSize = 50, timeout = 3000*/)
public class ChatController {

    @Autowired
    SessionService sessionService;

    @Autowired
    ChatService chatService;

    @WsMethod(uri = "/createRoom")
    public void createRoom(@WsSession WebSocketSession session) {
        WsSessionWrapper wrapper = sessionService.joinRoom(session, Define.DEMO_ROOM_ID);
        chatService.joinRoom(wrapper.getSessionId(), wrapper.getUser().getUserId());
    }

    @WsMethod(uri = "/joinRoom")
    public void joinRoom(@WsSession WebSocketSession session) {
        WsSessionWrapper wrapper = sessionService.joinRoom(session, Define.DEMO_ROOM_ID);
        chatService.joinRoom(wrapper.getSessionId(), wrapper.getUser().getUserId());
    }

    @WsMethod(uri = "/sendMessage")
    public void sendMessage(@WsSession WebSocketSession session, @Valid @WsRequestBody SendMessageRequest request) {
        WsSessionWrapper wrapper = sessionService.checkLoginAndGet(session);
        chatService.sendMessage(wrapper.getUser().getUserId(), request.getMessage());
    }

    @WsMethod(uri = "/leaveRoom")
    public void leaveRoom(@WsSession WebSocketSession session) {
        WsSessionWrapper wrapper = sessionService.leaveRoom(session);
        chatService.leaveRoom(wrapper.getSessionId());
    }

}
