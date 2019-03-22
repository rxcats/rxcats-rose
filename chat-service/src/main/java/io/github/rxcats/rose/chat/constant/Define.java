package io.github.rxcats.rose.chat.constant;

public class Define {
    public static final String CHAT_URI_PREFIX = "/chat/v1";
    public static final String SESSION_URI_PREFIX = "/session/v1";
    public static final String CHAT_ERROR_URI = CHAT_URI_PREFIX + "/error";
    public static final String CHAT_BROADCAST_URI = CHAT_URI_PREFIX + "/broadcast";
    public static final String CHAT_SYSTEM_URI = CHAT_URI_PREFIX + "/system";

    public static final String KEY_CHATROOM_TOPIC = "chatTopic";

    public static final String KEY_USER_INFO = "userInfo";
    public static final String KEY_ROOM_INFO = "roomInfo";

    private Define() {

    }
}
