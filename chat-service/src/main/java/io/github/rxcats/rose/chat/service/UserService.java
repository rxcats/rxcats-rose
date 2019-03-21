package io.github.rxcats.rose.chat.service;

import java.util.stream.IntStream;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

import io.github.rxcats.rose.chat.constant.Define;
import io.github.rxcats.rose.chat.ws.model.WsUser;

@Slf4j
@Service
public class UserService {

    @Autowired
    RedisTemplate<String, Object> redisTemplate;

    @PostConstruct
    public void initialize() {
        IntStream.rangeClosed(1, 10).forEach(i -> {
            var user = new WsUser();
            user.setUserId("" + (1000000000 + i));
            user.setUsername("Dummy#" + (1000000000 + i));
            user.setAvatar("https://api.adorable.io/avatars/65/abott@adorable.png");
            redisTemplate.opsForHash().put(Define.KEY_USER, user.getUserId(), user);
        });
    }

    public WsUser get(String userId) {
        return (WsUser) redisTemplate.opsForHash().get(Define.KEY_USER, userId);
    }

}
