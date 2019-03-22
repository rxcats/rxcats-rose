package io.github.rxcats.rose.chat.model.result;

import lombok.Data;

import io.github.rxcats.rose.chat.ws.WsSessionWrapper;

@Data
public class CheckJoinRoomResult {

    private boolean joined;

    private WsSessionWrapper wsSessionWrapper;

}
