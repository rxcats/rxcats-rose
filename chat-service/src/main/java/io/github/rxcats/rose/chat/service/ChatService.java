package io.github.rxcats.rose.chat.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

import io.github.rxcats.rose.chat.constant.Define;
import io.github.rxcats.rose.chat.model.message.BroadcastMessage;
import io.github.rxcats.rose.chat.redis.RedisMessagePublisher;
import io.github.rxcats.rose.chat.ws.WsSessionManager;
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

    public void createRoom(String id, String userId) {
        var member = new HashMap<String, String>();
        member.put("id", id);
        member.put("userId", userId);
        member.put("avater", "https://api.adorable.io/avatars/65/abott@adorable.png");
        redisTemplate.opsForHash().put(Define.DEMO_ROOM_ID, id, member);
    }

    public void joinRoom(String id, String userId) {
        var member = new HashMap<String, String>();
        member.put("id", id);
        member.put("userId", userId);
        member.put("avater", "https://api.adorable.io/avatars/65/abott@adorable.png");
        redisTemplate.opsForHash().put(Define.DEMO_ROOM_ID, id, member);

        publisher.publish(Define.KEY_CHATROOM, BroadcastMessage.of(Define.DEMO_ROOM_ID, "system", userId + " has joined."));
    }

    public void leaveRoom(String id) {
        Object o = redisTemplate.opsForHash().get(Define.DEMO_ROOM_ID, id);
        if (o != null) {
            // @formatter:off
            Map<String, String> user = objectMapper.convertValue(o, new TypeReference<Map<String, Object>>() {});
            // @formatter:on
            redisTemplate.opsForHash().delete(Define.DEMO_ROOM_ID, id);
            publisher.publish(Define.KEY_CHATROOM, BroadcastMessage.of(Define.DEMO_ROOM_ID, "system", user.get("userId") + " has left."));
        }
    }

    public void sendMessage(String senderId, String message) {
        publisher.publish(Define.KEY_CHATROOM, BroadcastMessage.of(Define.DEMO_ROOM_ID, senderId, message));
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
