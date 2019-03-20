package io.github.rxcats.rose.chat.constant;

public class Define {
    public static final String CHAT_URI_PREFIX = "/chat/v1";
    public static final String CHAT_ERROR_URI = CHAT_URI_PREFIX + "/error";
    public static final String CHAT_BROADCAST_URI = CHAT_URI_PREFIX + "/broadcast";
    public static final String CHAT_SYSTEM_URI = CHAT_URI_PREFIX + "/system";

    public static final String KEY_CHATROOM = "chatroom";
    public static final String DEMO_ROOM_ID = KEY_CHATROOM + ":1";

    private Define() {

    }
}
