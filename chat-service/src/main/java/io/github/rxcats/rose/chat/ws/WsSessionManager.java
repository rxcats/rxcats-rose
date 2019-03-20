package io.github.rxcats.rose.chat.ws;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

@Component
public class WsSessionManager {

    private static Map<String, WsSessionWrapper> wsSessionMap = new ConcurrentHashMap<>();

    public static void put(String key, WsSessionWrapper session) {
        wsSessionMap.put(key, session);
    }

    public static void remove(String key) {
        wsSessionMap.remove(key);
    }

    public static WsSessionWrapper get(String key) {
        return wsSessionMap.get(key);
    }

}
