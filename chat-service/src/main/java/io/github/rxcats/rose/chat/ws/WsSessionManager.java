package io.github.rxcats.rose.chat.ws;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class WsSessionManager {

    private static Map<String, WsSessionWrapper> wsSessionMap = new ConcurrentHashMap<>();

    private WsSessionManager() {

    }

    public static void put(String id, WsSessionWrapper session) {
        wsSessionMap.put(id, session);
    }

    public static void remove(String id) {
        wsSessionMap.remove(id);
    }

    public static WsSessionWrapper get(String id) {
        return wsSessionMap.get(id);
    }

    public static void clean() {
        wsSessionMap.forEach((id, session) -> {
            try {
                log.info("session clean: [session:{}]", session);
                session.getSession().close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        wsSessionMap.clear();
    }

}
