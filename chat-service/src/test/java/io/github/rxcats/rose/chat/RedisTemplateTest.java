package io.github.rxcats.rose.chat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import lombok.extern.slf4j.Slf4j;

import io.github.rxcats.rose.chat.model.UserInfo;

@Slf4j
@SpringBootTest
class RedisTemplateTest {

    @Autowired
    RedisTemplate<String, Object> redisTemplate;

    @Test
    void test() {
        var userInfo = (UserInfo) redisTemplate.opsForHash().get("userInfo", "1000000009");
        log.info("userInfo:{}", userInfo);
    }

}
