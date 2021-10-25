package cc.mudev.bca_android.activity.card;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.bumptech.glide.request.RequestOptions;

import java.util.HashMap;

import cc.mudev.bca_android.R;
import cc.mudev.bca_android.network.NetworkSupport;
import cc.mudev.bca_android.util.AlertDialogGenerator;
import cc.mudev.bca_android.util.image.ImageRotation;

public class CardDetailActivity extends AppCompatActivity {
    private static final String ERROR_DIALOG_TITLE = "명함을 자세히 볼 수 없습니다.";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_detail);

        Toolbar toolbar = findViewById(R.id.ac_cardDetail_toolbar);
        toolbar.setNavigationOnClickListener((view) -> {
            supportFinishAfterTransition();
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        });

        TextView cardNameText = findViewById(R.id.ac_cardDetail_cardNameText);
        ImageView cardImageView = findViewById(R.id.ac_cardDetail_cardImageView);

        Intent cardDetailIntent = getIntent();
        String cardNameString = cardDetailIntent.getStringExtra("cardName");
        String cardImageUrlString = cardDetailIntent.getStringExtra("cardImageUrl");
        boolean cardPrivateStatus = cardDetailIntent.getBooleanExtra("cardPrivateStatus", true);
        if (TextUtils.isEmpty(cardNameString) || TextUtils.isEmpty(cardImageUrlString)) {
            AlertDialogGenerator.gen(
                    CardDetailActivity.this,
                    ERROR_DIALOG_TITLE, "명함의 정보를 가져오는데 문제가 발생했습니다",
                    false, "확인", (d, i) -> {
                        finish();
                    });
            return;
        }
        cardNameText.setText(cardNameString + ((cardPrivateStatus) ? " - 비공개" : " - 공개"));

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

        Glide.with(CardDetailActivity.this)
                .load(new GlideUrl(api.baseUrl.substring(0, api.baseUrl.length() - 1) + cardImageUrlString, headerBuilder.build()))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .apply(options)
                .transform(new ImageRotation(270))
                .into(cardImageView);
    }
}
