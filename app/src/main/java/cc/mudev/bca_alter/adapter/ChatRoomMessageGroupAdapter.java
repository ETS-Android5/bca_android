package cc.mudev.bca_alter.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.app.ActivityOptionsCompat;
import androidx.core.util.Pair;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mikhaellopez.circularimageview.CircularImageView;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.TimeZone;

import cc.mudev.bca_alter.R;
import cc.mudev.bca_alter.activity.profile.ProfileDetailActivity;

public class ChatRoomMessageGroupAdapter extends RecyclerView.Adapter<ChatRoomMessageGroupAdapter.ChatRoomMessageGroupViewHolder> {
    public Context context;
    public ArrayList<ChatRoomMessageGroupData> chatRoomMessageGroupDataList;

    public class ChatRoomMessageGroupViewHolder extends RecyclerView.ViewHolder {
        // Normal message bubble
        public ArrayList<ChatRoomMessageData> messageChildData;
        public TextView profileName;
        public CircularImageView profileImage;
        public RecyclerView messageChildRecyclerView;
        public View itemView;

        // Info message bubble
        public TextView infoMessageText;

        ChatRoomMessageGroupViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;

            // All of these can be a null
            this.profileName = itemView.findViewById(R.id.li_chatRoomMessageGroup_profileName);
            this.profileImage = itemView.findViewById(R.id.li_chatRoomMessageGroup_profileImage);
            this.infoMessageText = itemView.findViewById(R.id.li_chatRoomMessageGroup_infoMessageText);
            this.messageChildRecyclerView = itemView.findViewById(R.id.li_chatRoomMessageGroup_messageBubbleRecyclerView);
            if (this.messageChildRecyclerView != null) {
                this.messageChildRecyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
            }
        }
    }

    public ChatRoomMessageGroupAdapter(Context context, ArrayList<ChatRoomMessageGroupData> list) {
        this.context = context;
        this.chatRoomMessageGroupDataList = list;
    }

    @Override
    public int getItemViewType(int position) {
        return chatRoomMessageGroupDataList.get(position).bubbleType.getLayout();
    }

    @Override
    public ChatRoomMessageGroupAdapter.ChatRoomMessageGroupViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(viewType, viewGroup, false);
        return new ChatRoomMessageGroupAdapter.ChatRoomMessageGroupViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ChatRoomMessageGroupAdapter.ChatRoomMessageGroupViewHolder holder, int position) {
        ChatRoomMessageGroupData chatMessageData = chatRoomMessageGroupDataList.get(position);

        switch (chatMessageData.bubbleType) {
            case MESSAGE_BUBBLE_MINE:
            case MESSAGE_BUBBLE_OTHERS:
                holder.profileName.setText(chatMessageData.profileName);
                holder.messageChildData = chatMessageData.chatRoomChildData;
                holder.messageChildRecyclerView.setAdapter(chatMessageData.chatRoomMessageAdapter);
                break;
            case INFO_BUBBLE:
                if (chatMessageData.infoMessage != null) {
                    holder.infoMessageText.setText(chatMessageData.infoMessage);
                } else if (chatMessageData.messageTime != null) {
                    // Maybe I'll regret raising minSdkVersion to 26 to use ZonedDateTime,
                    // but it'll work for now.
                    // TimeZone in chatMessageData.messageTime is UTC, so we need to change timezone.
                    ZonedDateTime messageTimeWithDeviceTZ = chatMessageData.messageTime.withZoneSameInstant(TimeZone.getDefault().toZoneId());
                    String dateDivisionMessageText = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일").format(messageTimeWithDeviceTZ);
                    holder.infoMessageText.setText(dateDivisionMessageText);
                }
                break;
            default:
                break;
        }
        if (holder.profileImage != null) {
            holder.profileImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent myProfileIntent = new Intent(v.getContext(), ProfileDetailActivity.class);

                    holder.profileImage.setTransitionName("profileImage");
                    Pair<View, String> pair_thumb = Pair.create(holder.profileImage, holder.profileImage.getTransitionName());
                    ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation((Activity) v.getContext(), pair_thumb);

                    v.getContext().startActivity(myProfileIntent, optionsCompat.toBundle());
                    holder.profileImage.setTransitionName(null);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return chatRoomMessageGroupDataList.size();
    }
}
