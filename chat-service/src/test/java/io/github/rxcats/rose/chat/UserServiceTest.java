package io.github.rxcats.rose.chat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import lombok.extern.slf4j.Slf4j;

import io.github.rxcats.rose.chat.model.UserInfo;
import io.github.rxcats.rose.chat.service.UserService;

@Slf4j
@SpringBootTest
class UserServiceTest {

    @Autowired
    UserService service;

    @Test
    void getUserTest() {
        UserInfo user = service.get("1000000001");
        log.info("user:{}", user);
    }

}
