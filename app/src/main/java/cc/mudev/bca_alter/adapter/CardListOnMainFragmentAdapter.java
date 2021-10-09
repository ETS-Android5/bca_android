package cc.mudev.bca_alter.adapter;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.core.app.ActivityOptionsCompat;
import androidx.core.util.Pair;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import cc.mudev.bca_alter.R;
import cc.mudev.bca_alter.activity.card.CardDetailActivity;
import cc.mudev.bca_alter.activity.main.MainActivity;
import cc.mudev.bca_alter.activity.profile.ProfileDetailActivity;


public class CardListOnMainFragmentAdapter extends RecyclerView.Adapter<CardListOnMainFragmentAdapter.CardListOnMainFragmentViewHolder> {
    private final ArrayList<ProfileDetailCardListData> cardListData;

    public class CardListOnMainFragmentViewHolder extends RecyclerView.ViewHolder {
        public ProfileDetailCardListData profileDetailCardListData;
        public TextView cardNameText;
        public ImageButton cardImageBtn;
        public View itemView;

        CardListOnMainFragmentViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;

            cardNameText = itemView.findViewById(R.id.li_cardListBig_cardNameText);
            cardImageBtn = itemView.findViewById(R.id.li_cardListBig_cardImageBtn);
        }
    }

    public CardListOnMainFragmentAdapter(ArrayList<ProfileDetailCardListData> list) {
        this.cardListData = list;
    }

    @Override
    public CardListOnMainFragmentViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.listitem_cardlist_big, viewGroup, false);
        return new CardListOnMainFragmentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CardListOnMainFragmentViewHolder holder, int position) {
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
