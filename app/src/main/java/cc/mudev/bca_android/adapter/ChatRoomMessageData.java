package cc.mudev.bca_android.adapter;

import java.time.ZonedDateTime;

public class ChatRoomMessageData {
    public boolean isMyMessage;
    public ZonedDateTime messageTime;
    public String messageBody;

    public ChatRoomMessageData(boolean isMyMessage, ZonedDateTime messageTime, String messageBody) {
        this.isMyMessage = isMyMessage;
        this.messageTime = messageTime;
        this.messageBody = messageBody;
    }
}
