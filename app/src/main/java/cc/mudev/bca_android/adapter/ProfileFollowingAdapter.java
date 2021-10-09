package cc.mudev.bca_android.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import cc.mudev.bca_android.R;

public class ProfileFollowingAdapter extends RecyclerView.Adapter<ProfileFollowingAdapter.ProfileFollowingViewHolder> {
    private final ArrayList<ProfileFollowingData> infoListData;

    public interface OnItemClickListener {
        void onItemClick(View view, int position) ;
    }

    private OnItemClickListener mListener = null ;

    // OnItemClickListener 리스너 객체 참조를 어댑터에 전달하는 메서드
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener ;
    }

    public class ProfileFollowingViewHolder extends RecyclerView.ViewHolder {
        public ProfileFollowingData profileFollowingData;
        public TextView profileNameText, profileDescriptionText;
        public View itemView;

        ProfileFollowingViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;

            profileNameText = itemView.findViewById(R.id.li_profileList_nameText);
            profileDescriptionText = itemView.findViewById(R.id.li_profileList_descriptionText);

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

    public ProfileFollowingAdapter(ArrayList<ProfileFollowingData> list) {
        infoListData = list;
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
        if ("".equals(infoData.description)) {
            holder.profileDescriptionText.setVisibility(View.GONE);
        } else {
            holder.profileDescriptionText.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return infoListData.size();
    }
}

