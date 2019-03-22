package io.github.rxcats.rose.chat.service;

import java.time.LocalDateTime;
import java.util.stream.IntStream;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

import io.github.rxcats.rose.chat.constant.Define;
import io.github.rxcats.rose.chat.model.UserInfo;

@Slf4j
@Service
public class UserService {

    @Autowired
    RedisTemplate<String, Object> redisTemplate;

    @PostConstruct
    public void initialize() {
        IntStream.rangeClosed(1, 10).forEach(i -> {
            var user = new UserInfo();
            user.setUserId("" + (1000000000 + i));
            user.setUsername("Dummy#" + (1000000000 + i));
            user.setAvatar("https://api.adorable.io/avatars/65/abott@adorable.png");
            user.setCreatedAt(LocalDateTime.now());
            user.setUpdatedAt(LocalDateTime.now());
            redisTemplate.opsForHash().put(Define.KEY_USER_INFO, user.getUserId(), user);
        });
    }

    public UserInfo create(UserInfo user) {
        user.setUpdatedAt(LocalDateTime.now());
        user.setCreatedAt(LocalDateTime.now());
        redisTemplate.opsForHash().put(Define.KEY_USER_INFO, user.getUserId(), user);
        return user;
    }

    public UserInfo get(String userId) {
        return (UserInfo) redisTemplate.opsForHash().get(Define.KEY_USER_INFO, userId);
    }

}
