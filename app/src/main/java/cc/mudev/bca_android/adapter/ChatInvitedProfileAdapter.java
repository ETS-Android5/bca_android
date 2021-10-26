package cc.mudev.bca_android.adapter;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;
import java.util.HashMap;

import cc.mudev.bca_android.R;
import cc.mudev.bca_android.network.NetworkSupport;

public class ChatInvitedProfileAdapter extends RecyclerView.Adapter<ChatInvitedProfileAdapter.ChatInvitedProfileViewHolder> {
    private final ArrayList<ChatInvitedProfileData> infoListData;

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    private ChatInvitedProfileAdapter.OnItemClickListener mListener = null;

    // OnItemClickListener 리스너 객체 참조를 어댑터에 전달하는 메서드
    public void setOnItemClickListener(ChatInvitedProfileAdapter.OnItemClickListener listener) {
        this.mListener = listener;
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

            itemView.setOnClickListener((view) -> {
                int pos = getAdapterPosition();
                if (pos != RecyclerView.NO_POSITION) {
                    if (mListener != null) {
                        mListener.onItemClick(view, pos);
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

        if (!TextUtils.isEmpty(infoData.imgSrc)) {
            NetworkSupport api = NetworkSupport.getInstance(null);

            RequestOptions options = new RequestOptions()
                    .centerCrop()
                    .diskCacheStrategyOf(DiskCacheStrategy.ALL)
                    .placeholder(R.drawable.ic_launcher_background)
                    .error(R.drawable.ic_launcher_background);
            LazyHeaders.Builder headerBuilder = new LazyHeaders.Builder();
            try {
                HashMap<String, String> headerData = api.getAuthenticatedHeader(false, true);
                for (String key : headerData.keySet())
                    headerBuilder = headerBuilder.addHeader(key, headerData.get(key));
            } catch (Exception e) {
            }

            Glide.with(holder.itemView)
                    .load(new GlideUrl(api.baseUrl.substring(0, api.baseUrl.length() - 1) + infoData.imgSrc, headerBuilder.build()))
                    .thumbnail()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .apply(options)
                    .dontAnimate()
                    .dontTransform()
                    .into(holder.profileImageView);
        } else {
            holder.profileImageView.setImageResource(R.drawable.ic_launcher_background);
        }
    }

    @Override
    public int getItemCount() {
        return infoListData.size();
    }
}

