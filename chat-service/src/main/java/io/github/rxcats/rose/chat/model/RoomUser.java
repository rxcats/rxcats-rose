package io.github.rxcats.rose.chat.model;

import lombok.Data;

@Data
public class RoomUser {

    private String sessionId;

    private String userId;

    public static RoomUser of(String sessionId, String userId) {
        var ru = new RoomUser();
        ru.sessionId = sessionId;
        ru.userId = userId;
        return ru;
    }

}
