package io.github.rxcats.rose.chat.redis;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;

import io.github.rxcats.rose.chat.constant.Define;

@Configuration
public class RedisPubSubConfig {

    @Autowired
    RedisMessageSubscriber subscriber;

    @Bean
    MessageListenerAdapter messageListenerAdapter() {
        return new MessageListenerAdapter(subscriber);
    }

    @Bean
    RedisMessageListenerContainer redisContainer(RedisConnectionFactory redisConnectionFactory) {
        var container = new RedisMessageListenerContainer();
        container.setConnectionFactory(redisConnectionFactory);
        container.addMessageListener(messageListenerAdapter(), List.of(ChannelTopic.of(Define.KEY_CHATROOM_TOPIC)));
        return container;
    }

}
