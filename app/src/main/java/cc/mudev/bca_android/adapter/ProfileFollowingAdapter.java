package cc.mudev.bca_android.adapter;

import android.content.Context;
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
import com.mikhaellopez.circularimageview.CircularImageView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cc.mudev.bca_android.R;
import cc.mudev.bca_android.database.ProfileDatabase;
import cc.mudev.bca_android.database.TB_PROFILE;
import cc.mudev.bca_android.network.NetworkSupport;
import cc.mudev.bca_android.util.image.ImageRotation;

public class ProfileFollowingAdapter extends RecyclerView.Adapter<ProfileFollowingAdapter.ProfileFollowingViewHolder> {
    public ArrayList<ProfileFollowingData> infoListData;

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    private OnItemClickListener mListener = null;

    // OnItemClickListener 리스너 객체 참조를 어댑터에 전달하는 메서드
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener;
    }

    public class ProfileFollowingViewHolder extends RecyclerView.ViewHolder {
        public View itemView;
        public ProfileFollowingData profileFollowingData;
        public TextView profileNameText, profileDescriptionText;
        public CircularImageView profileImage;

        ProfileFollowingViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;

            profileNameText = itemView.findViewById(R.id.li_profileList_nameText);
            profileDescriptionText = itemView.findViewById(R.id.li_profileList_descriptionText);
            profileImage = itemView.findViewById(R.id.li_profileList_profileImg);

            itemView.setOnClickListener((view) -> {
                int pos = getAdapterPosition();
                if (pos != RecyclerView.NO_POSITION) {
                    if (mListener != null)
                        mListener.onItemClick(view, pos);
                }
            });
        }
    }

    public ProfileFollowingAdapter() {
        infoListData = new ArrayList<>();
    }

    public void refreshData(Context context) {
        this.infoListData.clear();

        NetworkSupport networkSupport = NetworkSupport.getInstance(context);
        int currentProfileId = networkSupport.getCurrentProfileId();
        ProfileDatabase profileDatabase = ProfileDatabase.getInstance(context);
        List<Integer> targetProfileIdList = profileDatabase.profileRelationDao().getAllFollowingProfileId(currentProfileId);
        List<TB_PROFILE> targetProfileList = profileDatabase.profileDao().loadByProfileID(targetProfileIdList);

        for (TB_PROFILE targetProfile : targetProfileList)
            this.infoListData.add(new ProfileFollowingData(targetProfile.name, targetProfile.description, targetProfile.uuid, targetProfile.image_url));

        notifyDataSetChanged();
    }

    @Override
    public ProfileFollowingAdapter.ProfileFollowingViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.listitem_profilelist, viewGroup, false);
        return new ProfileFollowingAdapter.ProfileFollowingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ProfileFollowingAdapter.ProfileFollowingViewHolder holder, int position) {
        ProfileFollowingData infoData = infoListData.get(position);
        holder.profileFollowingData = infoData;
        holder.profileNameText.setText(infoData.name);
        holder.profileDescriptionText.setText(infoData.description);
        if (infoData.description == null || "".equals(infoData.description)) {
            holder.profileDescriptionText.setVisibility(View.GONE);
        } else {
            holder.profileDescriptionText.setVisibility(View.VISIBLE);
        }

        if (!TextUtils.isEmpty(infoData.imageUrl)) {
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
                    .load(new GlideUrl(api.baseUrl.substring(0, api.baseUrl.length() - 1) + infoData.imageUrl, headerBuilder.build()))
                    .thumbnail()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .apply(options)
                    .dontAnimate()
                    .dontTransform()
                    .into(holder.profileImage);
        } else {
            holder.profileImage.setImageResource(R.drawable.ic_launcher_background);
        }
    }

    @Override
    public int getItemCount() {
        return infoListData.size();
    }
}

