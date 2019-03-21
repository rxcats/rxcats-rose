package io.github.rxcats.rose.chat.ws.model;

import lombok.Data;

@Data
public class WsUser {

    private String userId;

    private String username;

    private String avatar;

    private Long loginTime;

    private String roomId;

    private Long joinTime;

}
