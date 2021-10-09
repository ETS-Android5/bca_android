package cc.mudev.bca_alter.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import cc.mudev.bca_alter.R;

public class ChatInvitedProfileAdapter extends RecyclerView.Adapter<ChatInvitedProfileAdapter.ChatInvitedProfileViewHolder> {
    private final ArrayList<ChatInvitedProfileData> infoListData;

    public interface OnItemClickListener {
        void onItemClick(View view, int position) ;
    }

    private ChatInvitedProfileAdapter.OnItemClickListener mListener = null ;

    // OnItemClickListener 리스너 객체 참조를 어댑터에 전달하는 메서드
    public void setOnItemClickListener(ChatInvitedProfileAdapter.OnItemClickListener listener) {
        this.mListener = listener ;
    }

    public class ChatInvitedProfileViewHolder extends RecyclerView.ViewHolder {
        public ChatInvitedProfileData chatInvitedProfileData;
        public TextView profileNameText;
        public ImageView profileImageView;
        public View itemView;

        ChatInvitedProfileViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;

            profileNameText = itemView.findViewById(R.id.li_chatCreateInvitedProfile_name);
            profileImageView = itemView.findViewById(R.id.li_chatCreateInvitedProfile_imgView);

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

    public ChatInvitedProfileAdapter(ArrayList<ChatInvitedProfileData> list) {
        infoListData = list;
    }

    @Override
    public ChatInvitedProfileAdapter.ChatInvitedProfileViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.listitem_chatcreate_invitedprofile, viewGroup, false);
        return new ChatInvitedProfileAdapter.ChatInvitedProfileViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ChatInvitedProfileAdapter.ChatInvitedProfileViewHolder holder, int position) {
        ChatInvitedProfileData infoData = infoListData.get(position);
        holder.chatInvitedProfileData = infoData;
        holder.profileNameText.setText(infoData.name);
//        holder.profileImageView.setImageURI(infoData.imgSrc);
    }

    @Override
    public int getItemCount() {
        return infoListData.size();
    }
}

