package cc.mudev.bca_android.adapter;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.core.app.ActivityOptionsCompat;
import androidx.core.util.Pair;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;
import java.util.HashMap;

import cc.mudev.bca_android.R;
import cc.mudev.bca_android.activity.card.CardDetailActivity;
import cc.mudev.bca_android.network.NetworkSupport;
import cc.mudev.bca_android.util.image.ImageRotation;


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

        if (!TextUtils.isEmpty(cardData.cardImageSrc)) {
            NetworkSupport api = NetworkSupport.getInstance(null);
            RequestOptions options = new RequestOptions()
                    .centerCrop()
                    .placeholder(R.drawable.card_loading)
                    .error(R.drawable.card_load_error);
            LazyHeaders.Builder headerBuilder = new LazyHeaders.Builder();
            try {
                HashMap<String, String> headerData = api.getAuthenticatedHeader(false, true);
                for (String key : headerData.keySet())
                    headerBuilder = headerBuilder.addHeader(key, headerData.get(key));
            } catch (Exception e) {
            }

            Glide.with(holder.itemView)
                    .load(new GlideUrl(api.baseUrl.substring(0, api.baseUrl.length() - 1) + cardData.cardImageSrc, headerBuilder.build()))
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .apply(options)
                    .transform(new ImageRotation(270))
                    .into(holder.cardImageBtn);
        } else {
            holder.cardImageBtn.setImageResource(R.drawable.card_self_404);
        }
        holder.cardNameText.setText(cardData.cardName);

        if (cardData.cardId > 0) {
            holder.cardImageBtn.setOnClickListener((v) -> {
                Intent cardDetailIntent = new Intent(v.getContext(), CardDetailActivity.class);

                Pair<View, String> pair_thumb = Pair.create(holder.cardImageBtn, holder.cardImageBtn.getTransitionName());
                ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation((Activity) v.getContext(), pair_thumb);
                cardDetailIntent.putExtra("cardName", cardData.cardName);
                cardDetailIntent.putExtra("cardImageUrl", cardData.cardImageSrc);
                cardDetailIntent.putExtra("cardPrivateStatus", cardData.cardPrivateStatus);

                v.getContext().startActivity(cardDetailIntent, optionsCompat.toBundle());
                ((Activity) v.getContext()).overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            });
        }
    }

    @Override
    public int getItemCount() {
        return cardListData.size();
    }
}
