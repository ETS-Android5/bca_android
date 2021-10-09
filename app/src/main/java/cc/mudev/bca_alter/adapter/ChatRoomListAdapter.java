package cc.mudev.bca_alter.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import cc.mudev.bca_alter.R;

public class ChatRoomListAdapter extends RecyclerView.Adapter<ChatRoomListAdapter.ChatListViewHolder> {
    private final ArrayList<ChatRoomListData> chatRoomListData;

    public interface OnItemClickListener {
        void onItemClick(View view, int position) ;
    }

    private OnItemClickListener mListener = null ;

    // OnItemClickListener 리스너 객체 참조를 어댑터에 전달하는 메서드
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener ;
    }

    public class ChatListViewHolder extends RecyclerView.ViewHolder {
        public ChatRoomListData chatRoomData;
        public TextView chatRoomNameText, chatRoomLatestMessageText, chatRoomUnseenMessageCountText;
        public ImageView chatRoomImage;
        public View itemView;

        ChatListViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;

            chatRoomNameText = itemView.findViewById(R.id.li_chatList_roomNameText);
            chatRoomLatestMessageText = itemView.findViewById(R.id.li_chatList_latestMessageText);
            chatRoomImage = itemView.findViewById(R.id.li_chatList_roomImg);
            chatRoomUnseenMessageCountText = itemView.findViewById(R.id.li_chatList_unseenMessageCountText);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = getAdapterPosition() ;
                    if (pos != RecyclerView.NO_POSITION) {
                        if (mListener != null) {
                            mListener.onItemClick(view, pos) ;
                        }
                    }
                }
            });
        }
    }

    public ChatRoomListAdapter(ArrayList<ChatRoomListData> list) {
        chatRoomListData = list;
    }

    @Override
    public ChatListViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.listitem_chatlist, viewGroup, false);
        return new ChatListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ChatListViewHolder holder, int position) {
        ChatRoomListData chatRoomData = chatRoomListData.get(position);
        holder.chatRoomData = chatRoomData;
        holder.chatRoomNameText.setText(chatRoomData.name);
        holder.chatRoomLatestMessageText.setText(chatRoomData.latestMessage);
        holder.chatRoomUnseenMessageCountText.setText(
            (chatRoomData.unseenMessageCount <= 300)
                ? Integer.toString(chatRoomData.unseenMessageCount)
                : "300+");
        if (chatRoomData.unseenMessageCount == 0) {
            holder.chatRoomUnseenMessageCountText.setVisibility(View.GONE);
        } else {
            holder.chatRoomUnseenMessageCountText.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return chatRoomListData.size();
    }
}

