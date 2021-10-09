package cc.mudev.bca_android.activity.card;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import cc.mudev.bca_android.R;

public class CardDetailActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_detail);

        Toolbar toolbar = findViewById(R.id.ac_cardDetail_toolbar);
        toolbar.setNavigationOnClickListener(new Toolbar.OnClickListener() {
            @Override
            public void onClick(View view) {
//                finish();
                supportFinishAfterTransition();
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });
    }
}
