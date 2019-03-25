package io.github.rxcats.rose.chat.service;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
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

    public String searchRoom() {
        Set<String> ids = redisTemplate.keys(Define.KEY_ROOM_INFO);
        if (CollectionUtils.isEmpty(ids)) {
            return null;
        } else {
            return ids.stream().findAny().orElse(null);
        }
    }

    public void createRoom(WsSessionWrapper wrapper) {
        String roomId = createRoomId();
        var roomUser = RoomUser.of(wrapper.getSessionId(), wrapper.getUser().getUserId());
        redisTemplate.opsForHash().put(roomId, wrapper.getSessionId(), roomUser);
        wrapper.joinRoom(roomId);
    }

    private String createRoomId() {
        return Define.KEY_ROOM_INFO + ":" + System.currentTimeMillis();
    }

    /**
     * key: roomInfo:{roomId}
     * hashKey: sessionId
     * value: members (sessionId, userId)
     */
    public void joinRoom(WsSessionWrapper wrapper, String roomId) {
        if (StringUtils.isEmpty(roomId)) {
            roomId = searchRoom();
            if (roomId == null) {
                roomId = createRoomId();
            }
        } else {
            Boolean check = redisTemplate.hasKey(roomId);
            if (check == null || !check) {
                throw new ServiceException("could not find room");
            }
        }

        var roomUser = RoomUser.of(wrapper.getSessionId(), wrapper.getUser().getUserId());
        redisTemplate.opsForHash().put(roomId, wrapper.getSessionId(), roomUser);
        wrapper.joinRoom(roomId);

        publisher.publish(Define.KEY_CHATROOM_TOPIC, BroadcastMessage.of(roomId, "system", wrapper.getUser().getUsername() + " has joined."));
    }

    public void leaveRoom(WsSessionWrapper wrapper, boolean force) {
        Boolean isJoined = redisTemplate.opsForHash().hasKey(wrapper.getUser().getRoomId(), wrapper.getSessionId());
        if (isJoined != null && isJoined) {
            if (!force) {
                wrapper.leaveRoom();
            }
            redisTemplate.opsForHash().delete(wrapper.getUser().getRoomId(), wrapper.getSessionId());
            publisher.publish(Define.KEY_CHATROOM_TOPIC, BroadcastMessage.of(wrapper.getUser().getRoomId(), "system", wrapper.getUser().getUsername() + " has left."));
        }
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

    @Scheduled(fixedDelay = 60_000)
    public void processClosedSession() {
        Map<String, WsSessionWrapper> allClosed = WsSessionManager.getAllClosed();
        log.info("allClosed:{}", allClosed);
        for (Entry<String, WsSessionWrapper> entry : allClosed.entrySet()) {
            WsSessionWrapper wrapper = WsSessionManager.removeClosed(entry.getKey());
            if (wrapper != null && entry.getValue().getUser() != null && entry.getValue().getUser().getRoomId() != null) {
                log.info("processClosedSession [session:{}]", wrapper.getSession());
                this.leaveRoom(entry.getValue(), true);
            }
        }
    }

}
