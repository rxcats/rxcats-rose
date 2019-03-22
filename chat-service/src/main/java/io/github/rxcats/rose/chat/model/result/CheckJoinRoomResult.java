package io.github.rxcats.rose.chat.model.result;

import lombok.Data;

@Data
public class CheckJoinRoomResult {

    private boolean joined;

    private String roomId;

}
