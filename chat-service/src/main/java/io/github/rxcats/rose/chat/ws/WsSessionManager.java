package io.github.rxcats.rose.chat.ws;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class WsSessionManager {

    private static Map<String, WsSessionWrapper> wsSessionMap = new ConcurrentHashMap<>();
    private static Map<String, WsSessionWrapper> closedSessionMap = new ConcurrentHashMap<>();

    private WsSessionManager() {

    }

    public static void put(String id, WsSessionWrapper session) {
        wsSessionMap.put(id, session);
    }

    public static WsSessionWrapper remove(String id) {
        return wsSessionMap.remove(id);
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

    public static void putClosed(String id, WsSessionWrapper session) {
        closedSessionMap.put(id, session);
    }

    public static WsSessionWrapper removeClosed(String id) {
        return closedSessionMap.remove(id);
    }

    public static Map<String, WsSessionWrapper> getAllClosed() {
        return closedSessionMap;
    }

    public static WsSessionWrapper getClosed(String id) {
        return closedSessionMap.get(id);
    }

    public static void cleanClosed() {
        closedSessionMap.clear();
    }

}
