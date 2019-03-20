package io.github.rxcats.rose.chat.model.message;

import lombok.Data;

@Data
public class BroadcastMessage {

    private String roomId;

    private String senderId;

    private String message;

    public static BroadcastMessage of(String roomId, String senderId, String message) {
        var bm = new BroadcastMessage();
        bm.roomId = roomId;
        bm.senderId = senderId;
        bm.message = message;
        return bm;
    }

}
