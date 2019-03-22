package io.github.rxcats.rose.chat.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

import io.github.rxcats.rose.chat.constant.Define;
import io.github.rxcats.rose.chat.exception.ServiceException;
import io.github.rxcats.rose.chat.model.RoomUser;
import io.github.rxcats.rose.chat.model.message.BroadcastMessage;
import io.github.rxcats.rose.chat.redis.RedisMessagePublisher;
import io.github.rxcats.rose.chat.ws.WsSessionManager;
import io.github.rxcats.rose.chat.ws.WsSessionWrapper;
import io.github.rxcats.rose.chat.ws.service.MessageService;

@Slf4j
@Service
public class ChatService {

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    RedisTemplate<String, Object> redisTemplate;

    @Autowired
    RedisMessagePublisher publisher;

    @Autowired
    MessageService messageService;

    public List<String> searchRoom() {
        Set<String> keys = redisTemplate.keys(Define.KEY_ROOM_INFO);
        if (CollectionUtils.isEmpty(keys)) {
            return Collections.emptyList();
        }
        return new ArrayList<>(keys);
    }

    /**
     * key: roomInfo:{roomId}
     * hashKey: sessionId
     * value: members (sessionId, userId)
     */
    public void joinRoom(WsSessionWrapper wrapper, String roomId) {
        if (StringUtils.isEmpty(roomId)) {
            List<String> roomIds = this.searchRoom();

            // 방이 없으면 신규 생성, 있으면 랜덤 선택
            if (roomIds.size() == 0) {
                roomId = Define.KEY_ROOM_INFO + ":" + System.currentTimeMillis();
            } else if (roomIds.size() == 1) {
                roomId = roomIds.get(0);
            } else {
                Collections.shuffle(roomIds);
                roomId = roomIds.get(0);
            }
        } else {
            Boolean check = redisTemplate.hasKey(roomId);
            if (check == null || !check) {
                throw new ServiceException("could not find room");
            }
        }

        wrapper.joinRoom(roomId);
        RoomUser roomUser = RoomUser.of(wrapper.getSessionId(), wrapper.getUser().getUserId());
        redisTemplate.opsForHash().put(roomId, wrapper.getSessionId(), roomUser);
        publisher.publish(Define.KEY_CHATROOM_TOPIC, BroadcastMessage.of(roomId, "system", wrapper.getUser().getUsername() + " has joined."));
    }

    public void leaveRoom(WsSessionWrapper wrapper) {
        RoomUser roomUser = (RoomUser) redisTemplate.opsForHash().get(wrapper.getUser().getRoomId(), wrapper.getSession());
        if (roomUser != null) {
            wrapper.leaveRoom();
            redisTemplate.opsForHash().delete(wrapper.getUser().getRoomId(), wrapper.getSession());
            publisher.publish(Define.KEY_CHATROOM_TOPIC, BroadcastMessage.of(wrapper.getUser().getRoomId(), "system", wrapper.getUser().getUsername() + " has left."));
        }
    }

    public void forceLeaveRoom(String sessionId, String roomId) {
        redisTemplate.opsForHash().delete(roomId, sessionId);
    }

    public void sendMessage(WsSessionWrapper wrapper, String message) {
        publisher.publish(Define.KEY_CHATROOM_TOPIC, BroadcastMessage.of(wrapper.getUser().getRoomId(), wrapper.getUser().getUserId(), message));
    }

    public void sendBroadcastMessage(BroadcastMessage bm) {
        Map<Object, Object> entries = redisTemplate.opsForHash().entries(bm.getRoomId());
        entries.forEach((id, user) -> {
            var wrapper = WsSessionManager.get((String) id);
            if (wrapper != null && wrapper.getSession() != null) {
                messageService.sendMessage(wrapper.getSession(), Define.CHAT_BROADCAST_URI, bm);
            }
        });
    }

}
