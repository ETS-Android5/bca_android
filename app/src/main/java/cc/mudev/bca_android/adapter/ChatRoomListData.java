package cc.mudev.bca_android.adapter;

public class ChatRoomListData implements Comparable<ChatRoomListData> {
    public int uuid;
    public String name, latestMessage;
    public int unseenMessageCount;

    public ChatRoomListData(int uuid, String name, String latestMessage, int unseenMessageCount) {
        this.uuid = uuid;
        this.name = name;
        this.latestMessage = latestMessage;
        this.unseenMessageCount = unseenMessageCount;
    }

    @Override
    public int compareTo(ChatRoomListData o) {
        if (o.unseenMessageCount < this.unseenMessageCount) {
            return -1;
        } else if (o.unseenMessageCount > this.unseenMessageCount) {
            return 1;
        }
        return 0;
    }
}
