package io.github.rxcats.rose.chat.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;

@Service
public class RedisMessagePublisher implements MessagePublisher {

    @Autowired
    RedisTemplate<String, Object> redisTemplate;

    @Override
    public void publish(String topic, Object message) {
        redisTemplate.convertAndSend(topic, message);
    }

}
