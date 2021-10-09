package cc.mudev.bca_alter.adapter;

import cc.mudev.bca_alter.R;

public enum ChatRoomMessageType {
    MESSAGE_BUBBLE_MINE(R.layout.listitem_chatroom_messagegroup_mine),
    MESSAGE_BUBBLE_OTHERS(R.layout.listitem_chatroom_messagegroup_other),
    INFO_BUBBLE(R.layout.listitem_chatroom_infobubble);

    private final int layout;
    ChatRoomMessageType(int layout) { this.layout = layout; }
    public int getLayout() { return this.layout; }
}
