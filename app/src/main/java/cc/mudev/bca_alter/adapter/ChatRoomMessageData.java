package cc.mudev.bca_alter.adapter;

import java.time.ZonedDateTime;

public class ChatRoomMessageData {
    boolean isMyMessage;
    ZonedDateTime messageTime;
    String messageBody;

    public ChatRoomMessageData(boolean isMyMessage, ZonedDateTime messageTime, String messageBody) {
        this.isMyMessage = isMyMessage;
        this.messageTime = messageTime;
        this.messageBody = messageBody;
    }
}
