package io.github.rxcats.rose.chat.redis;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

import io.github.rxcats.rose.chat.constant.Define;
import io.github.rxcats.rose.chat.model.message.BroadcastMessage;
import io.github.rxcats.rose.chat.service.ChatService;

@Slf4j
@Service
public class RedisMessageSubscriber implements MessageListener {

    @Autowired
    ChatService chatService;

    @Autowired
    ObjectMapper objectMapper;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        var topic = new String(pattern);

        if (log.isInfoEnabled()) {
            log.info("onMessage [topic:{}] [payload:{}]", topic, new String(message.getBody()));
        }

        if (Define.KEY_CHATROOM.equals(topic)) {

            try {
                BroadcastMessage bm = objectMapper.readValue(message.getBody(), BroadcastMessage.class);
                chatService.sendBroadcastMessage(bm);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }

}
