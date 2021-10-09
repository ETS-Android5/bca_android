package cc.mudev.bca_alter.adapter;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.app.ActivityOptionsCompat;
import androidx.core.util.Pair;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import cc.mudev.bca_alter.R;
import cc.mudev.bca_alter.activity.card.CardDetailActivity;


public class ProfileDetailCardListAdapter extends RecyclerView.Adapter<ProfileDetailCardListAdapter.ProfileDetailCardListViewHolder> {
    private final ArrayList<ProfileDetailCardListData> cardListData;

    public class ProfileDetailCardListViewHolder extends RecyclerView.ViewHolder {
        public ProfileDetailCardListData profileDetailCardListData;
        public TextView cardNameText;
        public ImageButton cardImageBtn;
        public View itemView;

        ProfileDetailCardListViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;

            cardNameText = itemView.findViewById(R.id.li_profileDetail_cardNameText);
            cardImageBtn = itemView.findViewById(R.id.li_profileDetail_cardImageBtn);
        }
    }

    public ProfileDetailCardListAdapter(ArrayList<ProfileDetailCardListData> list) { this.cardListData = list; }

    @Override
    public ProfileDetailCardListViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.listitem_profiledetail_card, viewGroup, false);
        return new ProfileDetailCardListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ProfileDetailCardListViewHolder holder, int position) {
        ProfileDetailCardListData cardData = cardListData.get(position);
        holder.profileDetailCardListData = cardData;
        if (cardData.cardImageSrc != null) {
//            holder.cardImageView.setImageDrawable(infoData.cardImageSrc);
        }
//        holder.cardImageView.setImageResource(R.drawable.card_test);
        holder.cardNameText.setText(cardData.cardName);

        holder.cardImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cardDetailIntent = new Intent(v.getContext(), CardDetailActivity.class);

                Pair<View, String> pair_thumb = Pair.create(holder.cardImageBtn, holder.cardImageBtn.getTransitionName());
                ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation((Activity) v.getContext(), pair_thumb);

                v.getContext().startActivity(cardDetailIntent, optionsCompat.toBundle());
                ((Activity) v.getContext()).overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });
    }

    @Override
    public int getItemCount() {
        return cardListData.size();
    }
}
