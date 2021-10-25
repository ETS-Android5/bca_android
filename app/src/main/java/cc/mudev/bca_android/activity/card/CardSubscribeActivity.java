package cc.mudev.bca_android.activity.card;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import cc.mudev.bca_android.R;

public class CardSubscribeActivity extends AppCompatActivity {
    private static final String ERROR_DIALOG_TITLE = "이 명함을 구독할 수 없어요";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_subscribe);
    }
}
