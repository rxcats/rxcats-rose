package io.github.rxcats.rose.chat.redis;

import org.springframework.data.redis.listener.ChannelTopic;

public interface MessagePublisher {
    void publish(String topic, Object message);
}
