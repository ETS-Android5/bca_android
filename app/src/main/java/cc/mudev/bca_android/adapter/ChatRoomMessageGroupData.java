package cc.mudev.bca_android.adapter;

import android.content.Context;

import java.time.ZonedDateTime;
import java.util.ArrayList;

public class ChatRoomMessageGroupData {
//    public int index;
    public Context context;
    public ChatRoomMessageType bubbleType;

    // For the time division bubble
    public ZonedDateTime messageTime;

    // For the info message bubble (attendant in/out, etc.)
    public String infoMessage;

    // For the normal message bubble
    public boolean isMyMessage;
    public int profileId;
    public String profileName;
    public ArrayList<ChatRoomMessageData> chatRoomChildData;
    public ChatRoomMessageAdapter chatRoomMessageAdapter;

    public ChatRoomMessageGroupData(
            Context context,
            boolean isMyMessage,
            int profileId,
            String profileName,
            ArrayList<ChatRoomMessageData> chatRoomChildData) {
        this.context = context;
        this.bubbleType = (isMyMessage) ? ChatRoomMessageType.MESSAGE_BUBBLE_MINE : ChatRoomMessageType.MESSAGE_BUBBLE_OTHERS;

        // Set time division bubble or info message bubble related variables as null
        this.messageTime = null;
        this.infoMessage = null;

        // Set normal message bubble
        this.isMyMessage = isMyMessage;
        this.profileId = profileId;
        this.profileName = profileName;
        this.chatRoomChildData = (chatRoomChildData != null) ? chatRoomChildData : new ArrayList<>();
        this.chatRoomMessageAdapter = new ChatRoomMessageAdapter(this.chatRoomChildData);
    }

    public ChatRoomMessageGroupData(Context context, ZonedDateTime messageTime) {
        this.context = context;
        this.bubbleType = ChatRoomMessageType.INFO_BUBBLE;

        // Set info message bubble or normal message bubble related variables as null
        this.infoMessage = null;
        this.isMyMessage = false;
        this.profileId = -1;
        this.profileName = null;
        this.chatRoomChildData = null;
        this.chatRoomMessageAdapter = null;

        // Set time division bubble
        this.messageTime = messageTime;
    }

    public ChatRoomMessageGroupData(Context context, String infoMessage) {
        this.context = context;
        this.bubbleType = ChatRoomMessageType.INFO_BUBBLE;

        // Set time division bubble or normal message bubble related variables as null
        this.messageTime = null;
        this.isMyMessage = false;
        this.profileName = null;
        this.chatRoomChildData = null;
        this.chatRoomMessageAdapter = null;

        // Set info message bubble
        this.infoMessage = infoMessage;
    }

    public ChatRoomMessageData getLastMessage() {
        if (this.chatRoomChildData != null && this.chatRoomChildData.size() > 0)
            return this.chatRoomChildData.get(this.chatRoomChildData.size() - 1);

        return null;
    }
}
