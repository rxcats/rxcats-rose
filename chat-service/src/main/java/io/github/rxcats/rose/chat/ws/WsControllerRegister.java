package io.github.rxcats.rose.chat.ws;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;

import lombok.extern.slf4j.Slf4j;

import io.github.rxcats.rose.chat.ws.annotation.WsController;
import io.github.rxcats.rose.chat.ws.annotation.WsMethod;

@Slf4j
@Configuration
public class WsControllerRegister {

    private static final Map<String, WsControllerEntity> wsControllerHolder = new ConcurrentHashMap<>();

    @Autowired
    private ApplicationContext applicationContext;

    @PostConstruct
    public void initialize() {
        Map<String, Object> list = applicationContext.getBeansWithAnnotation(WsController.class);

        list.forEach((k, v) -> {

            WsController clazz = v.getClass().getDeclaredAnnotation(WsController.class);
            String prefix = clazz.prefix();
            String groupName = clazz.groupName();
            if (groupName.isBlank()) {
                groupName = v.getClass().getSimpleName();
            }

            Method[] wsMethods = v.getClass().getMethods();

            var threadGroup = HystrixThreadGroup.builder()
                .desc(clazz.desc())
                .groupName(groupName)
                .threadSize(clazz.threadSize())
                .queueSize(clazz.queueSize())
                .timeout(clazz.timeout())
                .build();

            for (var method : wsMethods) {
                WsMethod wsMethod = method.getDeclaredAnnotation(WsMethod.class);
                if (wsMethod != null && !wsMethod.uri().isBlank()) {
                    wsControllerHolder.putIfAbsent(prefix + wsMethod.uri(), WsControllerEntity.builder()
                        .bean(v)
                        .method(method)
                        .hystrixThreadGroup(threadGroup)
                        .build());
                }
            }

        });

        wsControllerHolder.forEach((k, v) -> log.info("wsMapped [{}] [{}]", k, v));

    }

    public static WsControllerEntity getWsController(String uri) {
        return wsControllerHolder.get(uri);
    }
}
