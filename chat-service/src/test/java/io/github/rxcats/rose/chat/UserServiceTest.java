package io.github.rxcats.rose.chat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.web.WebAppConfiguration;

import lombok.extern.slf4j.Slf4j;

import io.github.rxcats.rose.chat.service.UserService;
import io.github.rxcats.rose.chat.ws.model.WsUser;

@Slf4j
@WebAppConfiguration
@SpringBootTest
class UserServiceTest {

    @Autowired
    UserService service;

    @Test
    void getUserTest() {
        WsUser user = service.get("1000000001");
        log.info("user:{}", user);
    }

}
