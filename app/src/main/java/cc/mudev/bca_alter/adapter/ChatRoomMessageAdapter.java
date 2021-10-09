package cc.mudev.bca_alter.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.TimeZone;

import cc.mudev.bca_alter.R;

public class ChatRoomMessageAdapter extends RecyclerView.Adapter<ChatRoomMessageAdapter.ChatRoomMessageViewHolder> {
    public ArrayList<ChatRoomMessageData> chatMessageDataList;

    public class ChatRoomMessageViewHolder extends RecyclerView.ViewHolder {
        public ChatRoomMessageData chatRoomMessageData;
        public View itemView;
        public TextView messageTimeText, messageBodyText;

        ChatRoomMessageViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;

            this.messageTimeText = itemView.findViewById(R.id.li_chatRoomMessageBubble_messageTimeText);
            this.messageBodyText = itemView.findViewById(R.id.li_chatRoomMessageBubble_messageBodyText);
        }
    }

    public ChatRoomMessageAdapter(ArrayList<ChatRoomMessageData> list) { this.chatMessageDataList = list; }

    @Override
    public int getItemViewType(int position) {
        ChatRoomMessageData chatRoomMessageData = chatMessageDataList.get(position);
        return (chatRoomMessageData.isMyMessage)
                ? R.layout.listitem_chatroom_messagebubble_mine
                : R.layout.listitem_chatroom_messagebubble_other;
    }

    @Override
    public ChatRoomMessageViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(viewType, viewGroup, false);
        return new ChatRoomMessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ChatRoomMessageViewHolder holder, int position) {
        ChatRoomMessageData chatMessageData = chatMessageDataList.get(position);
        ChatRoomMessageData nextChatMessageData = (position + 1 != chatMessageDataList.size())
                ? chatMessageDataList.get(position + 1)
                : null;

        if (position == 0) {
            if (chatMessageDataList.size() == 1) {
                if (chatMessageData.isMyMessage) {
                    holder.messageBodyText.setBackgroundResource(R.drawable.box_round_chatroom_messagebubble_mine);
                } else {
                    holder.messageBodyText.setBackgroundResource(R.drawable.box_round_chatroom_messagebubble_other);
                }
            } else {
                if (chatMessageData.isMyMessage) {
                    holder.messageBodyText.setBackgroundResource(R.drawable.box_round_chatroom_messagebubble_mine_first);
                } else {
                    holder.messageBodyText.setBackgroundResource(R.drawable.box_round_chatroom_messagebubble_other_first);
                }
            }
        } else if (position == chatMessageDataList.size() - 1) {
            if (chatMessageData.isMyMessage) {
                holder.messageBodyText.setBackgroundResource(R.drawable.box_round_chatroom_messagebubble_mine_last);
            } else {
                holder.messageBodyText.setBackgroundResource(R.drawable.box_round_chatroom_messagebubble_other_last);
            }
        }


        holder.chatRoomMessageData = chatMessageData;
        holder.messageBodyText.setText(chatMessageData.messageBody);

        // if chatMessageDataList length is 1, or this is the last message of this group, or current message time is different with next message time,
        // then show message time text
        if (chatMessageDataList.size() == 1 || nextChatMessageData == null || !chatMessageData.messageTime.equals(nextChatMessageData.messageTime)) {
            // TimeZone in chatMessageData.messageTime is UTC, so we need to change timezone.
            ZonedDateTime messageTimeWithDeviceTZ = chatMessageData.messageTime.withZoneSameInstant(TimeZone.getDefault().toZoneId());
            String dateDivisionMessageText = DateTimeFormatter.ofPattern("a hh시 mm분").format(messageTimeWithDeviceTZ);
            holder.messageTimeText.setText(dateDivisionMessageText);
        } else {
            holder.messageTimeText.setText("");
        }
    }

    @Override
    public int getItemCount() {
        return chatMessageDataList.size();
    }
}
