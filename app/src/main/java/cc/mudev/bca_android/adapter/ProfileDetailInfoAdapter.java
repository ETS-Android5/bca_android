package cc.mudev.bca_android.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import cc.mudev.bca_android.R;


public class ProfileDetailInfoAdapter extends RecyclerView.Adapter<ProfileDetailInfoAdapter.ProfileDetailInfoViewHolder> {
    private final ArrayList<ProfileDetailInfoData> infoListData;

    public class ProfileDetailInfoViewHolder extends RecyclerView.ViewHolder {
        public ProfileDetailInfoData profileDetailInfoData;
        public TextView infoTitleText, infoContentText;
        public View itemView;

        ProfileDetailInfoViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;

            infoTitleText = itemView.findViewById(R.id.li_profileDetail_infoTitleText);
            infoContentText = itemView.findViewById(R.id.li_profileDetail_infoContentText);
        }
    }

    public ProfileDetailInfoAdapter(ArrayList<ProfileDetailInfoData> list) { this.infoListData = list; }

    @Override
    public ProfileDetailInfoViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.listitem_profiledetail, viewGroup, false);
        return new ProfileDetailInfoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ProfileDetailInfoViewHolder holder, int position) {
        ProfileDetailInfoData infoData = infoListData.get(position);
        holder.profileDetailInfoData = infoData;
        holder.infoTitleText.setText(infoData.title);
        holder.infoContentText.setText(infoData.content);
    }

    @Override
    public int getItemCount() {
        return infoListData.size();
    }
}
